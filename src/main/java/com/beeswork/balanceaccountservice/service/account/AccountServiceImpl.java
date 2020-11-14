package com.beeswork.balanceaccountservice.service.account;


import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.question.QuestionDAO;
import com.beeswork.balanceaccountservice.dto.account.*;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.exception.question.QuestionNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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

    private final GeometryFactory geometryFactory;

    @Autowired
    public AccountServiceImpl(AccountDAO accountDAO,
                              ModelMapper modelMapper,
                              QuestionDAO questionDAO, GeometryFactory geometryFactory) {
        super(modelMapper);
        this.accountDAO = accountDAO;
        this.questionDAO = questionDAO;
        this.geometryFactory = geometryFactory;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
                   isolation = Isolation.READ_COMMITTED,
                   readOnly = true)
    public List<QuestionDTO> getQuestions(String accountId, String identityToken) {

        Account account = accountDAO.findWithQuestions(UUID.fromString(accountId), UUID.fromString(identityToken));
        checkIfAccountValid(account);

        List<QuestionDTO> questionDTOs = new ArrayList<>();
        for (AccountQuestion accountQuestion : account.getAccountQuestions()) {
            Question question = accountQuestion.getQuestion();
            questionDTOs.add(new QuestionDTO(accountQuestion.getQuestionId(),
                                             question.getDescription(),
                                             question.getTopOption(),
                                             question.getBottomOption()));
        }
        return questionDTOs;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
                   isolation = Isolation.READ_COMMITTED,
                   readOnly = true)
    public ProfileDTO getProfile(String accountId, String identityToken) {

        Account account = accountDAO.findWithPhotos(UUID.fromString(accountId), UUID.fromString(identityToken));
        checkIfAccountValid(account);
        return modelMapper.map(account, ProfileDTO.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
                   isolation = Isolation.READ_COMMITTED,
                   readOnly = true)
    public List<PhotoDTO> getPhotos(String accountId, String identityToken) {

        Account account = accountDAO.findWithPhotos(UUID.fromString(accountId), UUID.fromString(identityToken));
        checkIfAccountValid(account);
        return modelMapper.map(account.getPhotos(), new TypeToken<List<PhotoDTO>>() {
        }.getType());
    }

    //  DESC 1. when registering, an account will be created with enabled = false, then when finish profiles,
    //          it will update enabled = true because users might get cards for which profile has not been updated
    //  TEST 2. save account without any changes but changes in accountQuestions --> Hibernate does not publish update DML for unchanged account even if you change accountQuestions
    //  TEST 3. save account without accountQuestions with modemapper --> modelmapper call setAccountQuestions and Hibernate recognizes this call and
    //          update persistence context which will delete all accountQuestions. Without modelmapper and update account fields only
    //          then Hibernate won't delete accountQuestions even if their size = 0
    @Override
    @Transactional
    public void saveProfile(String accountId, String identityToken, String name, String email, Date birth, String about,
                            Integer height, boolean gender) {

        Account account = accountDAO.findBy(UUID.fromString(accountId), UUID.fromString(identityToken));
        checkIfAccountValid(account);

        if (!account.isEnabled()) {
            account.setName(name);
            account.setEmail(email);

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

        Account account = accountDAO.findBy(UUID.fromString(accountId), UUID.fromString(identityToken));
        checkIfAccountValid(account);

        account.setHeight(height);
        account.setAbout(about);
        account.setUpdatedAt(new Date());
    }

    @Override
    @Transactional
    public void saveLocation(String accountId, String identityToken, double latitude, double longitude,
                             Date updatedAt) {

        Account account = accountDAO.findBy(UUID.fromString(accountId), UUID.fromString(identityToken));
        checkIfAccountValid(account);
        saveLocation(account, latitude, longitude, updatedAt);
    }

    private void saveLocation(Account account, double latitude, double longitude, Date updatedAt) {
        account.setLocation(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
        account.setLocationUpdatedAt(updatedAt);
    }

    @Override
    @Transactional
    public void saveFCMToken(String accountId, String identityToken, String token) {

        Account account = accountDAO.findBy(UUID.fromString(accountId), UUID.fromString(identityToken));
        checkIfAccountValid(account);
        account.setFcmToken(token);
    }

    //  TEST 1. save accountQuestionDTOs without setAccount() and setQuestion() --> Hibernate does not insert those objects, no exception thrown
    //  TEST 2. create new accountQuestionDTO with the same AccountQuestionId --> Hibernate throws exception of creating two object with the same Id
    //  DESC 3. I don't need to put accountQuestionDTO or accountQuestion because Hibernate anyway need a whole list of
    //          accountQuestions to check if it needs to remove or insert or update entities
    @Override
    @Transactional
    public void saveAnswers(String accountId, String identityToken, Map<Long, Boolean> answers) {

        List<Long> questionIds = new ArrayList<>(answers.keySet());

        Account account = accountDAO.findWithAccountQuestionsWithQuestionIdIn(UUID.fromString(accountId),
                                                                              UUID.fromString(identityToken),
                                                                              questionIds);
        checkIfAccountValid(account);

        Map<Long, Integer> sequences = new LinkedHashMap<>();
        int sequence = 1;

        for (Long key : answers.keySet()) {
            sequences.put(key, sequence);
            sequence++;
        }

        for (int i = account.getAccountQuestions().size() - 1; i >= 0; i--) {
            AccountQuestion accountQuestion = account.getAccountQuestions().get(i);


            long questionId = accountQuestion.getQuestionId();

            if (answers.containsKey(questionId)) {

                accountQuestion.setSelected(true);
                accountQuestion.setAnswer(answers.get(questionId));
                accountQuestion.setSequence(sequences.get(questionId));
                accountQuestion.setUpdatedAt(new Date());

                answers.remove(questionId);
                sequences.remove(questionId);
                questionIds.remove(questionId);
            } else {
                accountQuestion.setSelected(false);
            }


        }

        if (questionIds.size() > 0) {

            List<Question> questions = questionDAO.findAllByIds(questionIds);

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
    public PreRecommendDTO preRecommend(String accountId, String identityToken, Double latitude, Double longitude,
                                        Date locationUpdatedAt, boolean reset) {

        Account account = accountDAO.findBy(UUID.fromString(accountId), UUID.fromString(identityToken));
        checkIfAccountValid(account);

//        int index = reset ? 0 : account.getIndex() + 1;
//        account.setIndex(index);

        if (latitude != null && longitude != null && locationUpdatedAt.after(account.getLocationUpdatedAt()))
            saveLocation(account, latitude, longitude, locationUpdatedAt);

        PreRecommendDTO preRecommendDTO = new PreRecommendDTO();
        preRecommendDTO.setIndex(account.getIndex());
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
            cardDTO.getPhotos().add(cAccount[CARD_PHOTO_KEY].toString());
        }

        cardDTOs.add(cardDTO);
        cardDTOs.remove(0);
        return cardDTOs;
    }

}
