package com.beeswork.balanceaccountservice.service.account;


import com.beeswork.balanceaccountservice.constant.AppConstant;
import com.beeswork.balanceaccountservice.constant.ColumnIndex;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.question.QuestionDAO;
import com.beeswork.balanceaccountservice.dto.account.*;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.exception.account.AccountBlockedException;
import com.beeswork.balanceaccountservice.exception.question.QuestionNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
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
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl extends BaseServiceImpl implements AccountService {

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
    public List<QuestionDTO> getQuestions(String accountId, String email) {

        Account account = accountDAO.findWithQuestions(UUID.fromString(accountId), email);
        checkIfAccountValid(account);

        List<QuestionDTO> questionDTOs = new ArrayList<>();
        for (AccountQuestion accountQuestion : account.getAccountQuestions()) {
            Question question = accountQuestion.getQuestion();
            questionDTOs.add(new QuestionDTO(accountQuestion.getQuestionId(),
                                             question.getDescription(),
                                             question.getTopOption(),
                                             question.getBottomOption(),
                                             accountQuestion.isSelected()));
        }
        return questionDTOs;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
                   isolation = Isolation.READ_COMMITTED,
                   readOnly = true)
    public ProfileDTO getProfile(String accountId, String email) {

        Account account = accountDAO.findWithPhotos(UUID.fromString(accountId), email);
        checkIfAccountValid(account);
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
    public void saveProfile(String accountId, String email, String name, Date birth, String about, Integer height, boolean gender) {

        Account account = accountDAO.findBy(UUID.fromString(accountId), email);
        checkIfAccountValid(account);

        if (!account.isEnabled()) {
            account.setName(name);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(account.getBirth());

            account.setBirthYear(calendar.get(Calendar.YEAR));
            account.setBirth(birth);
            account.setGender(gender);
            account.setHeight(height);
        }

        account.setAbout(about);
        account.setUpdatedAt(new Date());
        account.setEnabled(true);
    }

    @Override
    @Transactional
    public void saveLocation(String accountId, String email, double latitude, double longitude) {

        Account account = accountDAO.findBy(UUID.fromString(accountId), email);
        checkIfAccountValid(account);
        account.setLocation(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
    }

    @Override
    @Transactional
    public void saveFCMToken(String accountId, String email, String token) {

        Account account = accountDAO.findBy(UUID.fromString(accountId), email);
        checkIfAccountValid(account);
        account.setFcmToken(token);
    }

    //  TEST 1. save accountQuestionDTOs without setAccount() and setQuestion() --> Hibernate does not insert those objects, no exception thrown
    //  TEST 2. create new accountQuestionDTO with the same AccountQuestionId --> Hibernate throws exception of creating two object with the same Id
    //  DESC 3. I don't need to put accountQuestionDTO or accountQuestion because Hibernate anyway need a whole list of
    //          accountQuestions to check if it needs to remove or insert or update entities
    @Override
    @Transactional
    public void saveAnswers(String accountId, String email, Map<Long, Boolean> answers) {

        Account account = accountDAO.findWithAccountQuestions(UUID.fromString(accountId), email);
        checkIfAccountValid(account);

        account.getAccountQuestions().clear();
        Date date = new Date();

        List<Long> questionIds = new ArrayList<>(answers.keySet());
        List<Question> questions = questionDAO.findAllByIds(questionIds);

        if (answers.size() != questions.size())
            throw new QuestionNotFoundException();

        for (Question question : questions) {
            Boolean answer = answers.get(question.getId());
            if (answer == null) throw new QuestionNotFoundException();
            account.getAccountQuestions().add(new AccountQuestion(account, question, answer, date, date));
        }
    }

    // TEST 1. matches are mapped by matcher_id not matched_id
    @Override
    @Transactional(propagation = Propagation.REQUIRED,
                   isolation = Isolation.READ_COMMITTED,
                   readOnly = true)
    public List<CardDTO> recommend(String accountId, String email, int distance, int minAge, int maxAge, boolean gender, double latitude, double longitude) {

        Account account = accountDAO.findBy(UUID.fromString(accountId), email);
        checkIfAccountValid(account);
        Point location = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        location.setSRID(AppConstant.SRID);
        List<Object[]> accounts = accountDAO.findAllWithin(distance, minAge, maxAge, gender, AppConstant.PAGE_LIMIT,
                                                           account.getIndex() * AppConstant.PAGE_LIMIT, location);

        //increment or reset index
        int index = account.getIndex() + 1;
//        if (accounts.size() < AppConstant.PAGE_LIMIT)
//            index = 0;
        account.setIndex(index);

        String previousId = "";
        List<CardDTO> cardDTOs = new ArrayList<>();
        CardDTO cardDTO = new CardDTO();

        for (Object[] cAccount : accounts) {
            String id = cAccount[ColumnIndex.CARD_ID].toString();
            if (!previousId.equals(id)) {

                cardDTOs.add(cardDTO);
                previousId = id;

                String name = cAccount[ColumnIndex.CARD_NAME].toString();
                String about = cAccount[ColumnIndex.CARD_ABOUT].toString();
                int birthYear = Integer.parseInt(cAccount[ColumnIndex.CARD_BIRTH_YEAR].toString());
                int distanceBetween = (int) ((double) cAccount[ColumnIndex.CARD_DISTANCE]);
                Integer height = (Integer) cAccount[ColumnIndex.CARD_HEIGHT];

                cardDTO = new CardDTO(id, name, about, height, birthYear, distanceBetween);
            }
            cardDTO.getPhotos().add(cAccount[ColumnIndex.CARD_PHOTO_KEY].toString());
        }

        cardDTOs.add(cardDTO);
        cardDTOs.remove(0);

        throw new AccountBlockedException();
//        return cardDTOs;
    }

}
