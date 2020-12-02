package com.beeswork.balanceaccountservice.service.account;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.beeswork.balanceaccountservice.config.properties.AWSProperties;
import com.beeswork.balanceaccountservice.constant.AccountType;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.question.QuestionDAO;
import com.beeswork.balanceaccountservice.dto.account.*;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.photo.Photo;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.exception.account.AccountEmailNotMutableException;
import com.beeswork.balanceaccountservice.exception.question.QuestionNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import com.beeswork.balanceaccountservice.vm.account.AccountEmailDuplicateException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AccountServiceImpl extends BaseServiceImpl implements AccountService {

    private static final int CARD_ID = 0;
    private static final int CARD_NAME = 1;
    private static final int CARD_ABOUT = 2;
    private static final int CARD_BIRTH_YEAR = 3;
    private static final int CARD_DISTANCE = 4;
    private static final int CARD_PHOTO_KEY = 5;
    private static final int CARD_HEIGHT = 6;

    private static final int PAGE_LIMIT = 15;

    private static final int maxDistance = 10000;
    private static final int minDistance = 1000;

    private final AccountDAO accountDAO;
    private final QuestionDAO questionDAO;
    private final AmazonS3 amazonS3;
    private final AWSProperties awsProperties;

    private final GeometryFactory geometryFactory;

    @Autowired
    public AccountServiceImpl(AccountDAO accountDAO,
                              ModelMapper modelMapper,
                              QuestionDAO questionDAO, AmazonS3 amazonS3, AWSProperties awsProperties, GeometryFactory geometryFactory) {
        super(modelMapper);
        this.accountDAO = accountDAO;
        this.questionDAO = questionDAO;
        this.amazonS3 = amazonS3;
        this.awsProperties = awsProperties;
        this.geometryFactory = geometryFactory;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED,
                   isolation = Isolation.READ_COMMITTED,
                   readOnly = true)
    public ProfileDTO getProfile(String accountId, String identityToken) {

        Account account = findValidAccount(accountId, identityToken);
        return modelMapper.map(account, ProfileDTO.class);
    }


    //  DESC 1. when registering, an account will be created with enabled = false, then when finish profiles,
    //          it will update enabled = true because users might get cards for which profile has not been updated
    //  TEST 2. save account without any changes but changes in accountQuestions --> Hibernate does not publish update DML for unchanged account even if you change accountQuestions
    //  TEST 3. save account without accountQuestions with modemapper --> modelmapper call setAccountQuestions and Hibernate recognizes this call and
    //          update persistence context which will delete all accountQuestions. Without modelmapper and update account fields only
    //          then Hibernate won't delete accountQuestions even if their size = 0
    @Override
    @Transactional
    public void saveProfile(String accountId, String identityToken, String name, Date birth, String about,
                            Integer height, boolean gender) {

        Account account = findValidAccount(accountId, identityToken);

        if (account.isEnabled()) {
            account.setName(name);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(account.getBirth());

            account.setBirthYear(calendar.get(Calendar.YEAR));
            account.setBirth(birth);
            account.setGender(gender);
            account.setHeight(height);
            account.setAbout(about);
            account.setUpdatedAt(new Date());
            account.setEnabled(true);
        }
    }

    @Override
    @Transactional
    public void saveAbout(String accountId, String identityToken, String about, Integer height) {

        Account account = findValidAccount(accountId, identityToken);
        account.setHeight(height);
        account.setAbout(about);
        account.setUpdatedAt(new Date());
    }

    @Override
    @Transactional
    public void saveLocation(String accountId, String identityToken, double latitude, double longitude,
                             Date updatedAt) {

        Account account = findValidAccount(accountId, identityToken);
        saveLocation(account, latitude, longitude, updatedAt);
    }

    private void saveLocation(Account account, double latitude, double longitude, Date updatedAt) {
        account.setLocation(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
        account.setLocationUpdatedAt(updatedAt);
    }

    @Override
    @Transactional
    public void saveFCMToken(String accountId, String identityToken, String token) {

        Account account = findValidAccount(accountId, identityToken);
        account.setFcmToken(token);
    }

    //  TEST 1. save accountQuestionDTOs without setAccount() and setQuestion() --> Hibernate does not insert those objects, no exception thrown
    //  TEST 2. create new accountQuestionDTO with the same AccountQuestionId --> Hibernate throws exception of creating two object with the same Id
    //  DESC 3. I don't need to put accountQuestionDTO or accountQuestion because Hibernate anyway need a whole list of
    //          accountQuestions to check if it needs to remove or insert or update entities
    @Override
    @Transactional
    public void saveAnswers(String accountId, String identityToken, Map<Integer, Boolean> answers) {

        Account account = accountDAO.findWithAccountQuestionsIn(UUID.fromString(accountId),
                                                                UUID.fromString(identityToken),
                                                                answers.keySet());
        checkIfAccountValid(account);

        Map<Integer, Integer> sequences = new LinkedHashMap<>();
        int sequence = 1;

        for (Integer key : answers.keySet()) {
            sequences.put(key, sequence);
            sequence++;
        }

        for (int i = account.getAccountQuestions().size() - 1; i >= 0; i--) {

            AccountQuestion accountQuestion = account.getAccountQuestions().get(i);
            int questionId = accountQuestion.getQuestionId();

            if (answers.containsKey(questionId)) {

                accountQuestion.setSelected(true);
                accountQuestion.setAnswer(answers.get(questionId));
                accountQuestion.setSequence(sequences.get(questionId));
                accountQuestion.setUpdatedAt(new Date());

                answers.remove(questionId);
                sequences.remove(questionId);
            } else {
                accountQuestion.setSelected(false);
            }
        }

        if (answers.size() > 0) {

            List<Question> questions = questionDAO.findAllByIds(answers.keySet());

            if (answers.size() != questions.size())
                throw new QuestionNotFoundException();

            for (Question question : questions) {
                Date date = new Date();
                account.getAccountQuestions()
                       .add(new AccountQuestion(true,
                                                answers.get(question.getId()),
                                                sequences.get(question.getId()),
                                                date,
                                                date,
                                                account,
                                                question));
            }
        }
    }

    @Override
    @Transactional
    public void saveEmail(String accountId, String identityToken, String email) {

        Account account = findValidAccount(accountId, identityToken);

        if (account.getAccountType() == AccountType.NAVER || account.getAccountType() == AccountType.GOOGLE)
            throw new AccountEmailNotMutableException();

        if (accountDAO.existsByEmail(email))
            throw new AccountEmailDuplicateException();

        account.setEmail(email);
    }

    @Override
    @Transactional
    public PreRecommendDTO preRecommend(String accountId, String identityToken, Double latitude, Double longitude,
                                        Date locationUpdatedAt, boolean reset) {

        Account account = findValidAccount(accountId, identityToken);

        PreRecommendDTO preRecommendDTO = new PreRecommendDTO();

        int index = reset ? 0 : account.getIndex();
        preRecommendDTO.setIndex(index);
        index++;
        account.setIndex(index);

        if (latitude != null &&
            longitude != null &&
            locationUpdatedAt != null &&
            locationUpdatedAt.after(account.getLocationUpdatedAt()))
            saveLocation(account, latitude, longitude, locationUpdatedAt);

        preRecommendDTO.setLocation(account.getLocation());
        return preRecommendDTO;
    }


    // TEST 1. matches are mapped by matcher_id not matched_id
    @Override
    @Transactional(propagation = Propagation.REQUIRED,
                   isolation = Isolation.READ_COMMITTED,
                   readOnly = true)
    public List<CardDTO> recommend(int distance, int minAge, int maxAge, boolean gender, Point location, int index) {

        if (distance < minDistance || distance > maxDistance)
            distance = maxDistance;

        List<Object[]> accounts = accountDAO.findAllWithin(distance, minAge, maxAge, gender, PAGE_LIMIT,
                                                           index * PAGE_LIMIT, location);

        String previousId = "";
        List<CardDTO> cardDTOs = new ArrayList<>();
        CardDTO cardDTO = new CardDTO();

        for (Object[] cAccount : accounts) {
            String id = cAccount[CARD_ID].toString();
            if (!previousId.equals(id)) {

                cardDTOs.add(cardDTO);
                previousId = id;

                String name = cAccount[CARD_NAME].toString();
                String about = cAccount[CARD_ABOUT].toString();
                int birthYear = Integer.parseInt(cAccount[CARD_BIRTH_YEAR].toString());
                int distanceBetween = (int) ((double) cAccount[CARD_DISTANCE]);
                Integer height = (Integer) cAccount[CARD_HEIGHT];

                cardDTO = new CardDTO(id, name, about, height, birthYear, distanceBetween);
            }

            if (cAccount[CARD_PHOTO_KEY] != null)
                cardDTO.getPhotos().add(cAccount[CARD_PHOTO_KEY].toString());
        }

        cardDTOs.add(cardDTO);
        cardDTOs.remove(0);
        return cardDTOs;
    }

    @Override
    @Transactional
    public void deleteAccount(String accountId, String identityToken) {

        Account account = findValidAccount(accountId, identityToken);


        // delete photos in s3
        ArrayList<DeleteObjectsRequest.KeyVersion> keys = new ArrayList<>();

        for (Photo photo : account.getPhotos()) {
            keys.add(new DeleteObjectsRequest.KeyVersion(account.getId().toString() + "/" + photo.getKey()));
        }

        DeleteObjectsRequest deleteObjectsRequest =
                new DeleteObjectsRequest(awsProperties.getBalancePhotoBucket()).withKeys(keys).withQuiet(true);

        amazonS3.deleteObjects(deleteObjectsRequest);


        // delete photos in database
        account.getPhotos().clear();

        // delete account_questions
        account.getAccountQuestions().clear();

        // delete profiles
        Date today = new Date();

        account.setDeleted(true);
        account.setSocialLoginId(null);
        account.setIdentityToken(null);
        account.setName("");
        account.setEmail("");
        account.setHeight(0);
        account.setBirthYear(0);
        account.setBirth(today);
        account.setAbout("");
        account.setScore(0);
        account.setIndex(0);
        account.setPoint(0);
        account.setLocation(null);
        account.setRepPhotoKey(null);
        account.setLocationUpdatedAt(today);
        account.setFcmToken(null);
        account.setUpdatedAt(today);
    }

    private Account findValidAccount(String accountId, String identityToken) {
        Account account = accountDAO.findBy(UUID.fromString(accountId), UUID.fromString(identityToken));
        checkIfAccountValid(account);
        return account;
    }
}
