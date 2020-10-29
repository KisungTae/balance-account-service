package com.beeswork.balanceaccountservice.service.account;


import com.beeswork.balanceaccountservice.constant.AppConstant;
import com.beeswork.balanceaccountservice.constant.ColumnIndex;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.question.QuestionDAO;
import com.beeswork.balanceaccountservice.dto.account.*;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.question.Question;
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


    //  DESC 1. when registering, an account will be created with enabled = false, then when finish profiles,
    //          it will update enabled = true because users might get cards for which profile has not been updated
    //  TEST 2. save account without any changes but changes in accountQuestions --> Hibernate does not publish update DML for unchanged account even if you change accountQuestions
    //  TEST 3. save account without accountQuestions with modemapper --> modelmapper call setAccountQuestions and Hibernate recognizes this call and
    //          update persistence context which will delete all accountQuestions. Without modelmapper and update account fields only
    //          then Hibernate won't delete accountQuestions even if their size = 0
    @Override
    @Transactional
    public void saveProfile(String accountId, String email, String name, Date birth, String about, boolean gender) {

        System.out.println("execute saveProfile() with accountId: " + accountId + " || email: " + email);

        Account account = accountDAO.findBy(UUID.fromString(accountId), email);
        checkIfValid(account);

        if (!account.isEnabled()) {
            account.setName(name);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(account.getBirth());

            account.setBirthYear(calendar.get(Calendar.YEAR));
            account.setBirth(birth);
            account.setGender(gender);
        }

        account.setAbout(about);
        account.setUpdatedAt(new Date());
        account.setEnabled(true);
    }

    @Override
    @Transactional
    public void saveLocation(String accountId, String email, double latitude, double longitude) {

        Account account = accountDAO.findBy(UUID.fromString(accountId), email);
        checkIfValid(account);
        account.setLocation(geometryFactory.createPoint(new Coordinate(latitude, longitude)));
    }

    @Override
    @Transactional
    public void saveFCMToken(String accountId, String email, String token) {

        Account account = accountDAO.findBy(UUID.fromString(accountId), email);
        checkIfValid(account);
        account.setFcmToken(token);
    }

    //  TEST 1. save accountQuestionDTOs without setAccount() and setQuestion() --> Hibernate does not insert those objects, no exception thrown
    //  TEST 2. create new accountQuestionDTO with the same AccountQuestionId --> Hibernate throws exception of creating two object with the same Id
    //  DESC 3. I don't need to put accountQuestionDTO or accountQuestion because Hibernate anyway need a whole list of
    //          accountQuestions to check if it needs to remove or insert or update entities
    @Override
    @Transactional
    public void saveQuestions(String accountId, String email, List<AccountQuestionDTO> accountQuestionDTOs) {

        Account account = accountDAO.findWithAccountQuestions(UUID.fromString(accountId), email);
        checkIfValid(account);
        account.getAccountQuestions().clear();
        Date date = new Date();

        for (AccountQuestionDTO accountQuestionDTO : accountQuestionDTOs) {
            Question question = questionDAO.findById(accountQuestionDTO.getQuestionId());
            if (question == null) throw new QuestionNotFoundException();
            account.getAccountQuestions().add(new AccountQuestion(account,
                                                                  question,
                                                                  accountQuestionDTO.isSelected(),
                                                                  accountQuestionDTO.getSequence(),
                                                                  date,
                                                                  date));
        }
    }

    // TEST 1. matches are mapped by matcher_id not matched_id
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<CardDTO> recommend(String accountId, String email, int distance, int minAge, int maxAge, boolean gender, double latitude, double longitude) {

        Account account = accountDAO.findBy(UUID.fromString(accountId), email);
        checkIfValid(account);
        Point location = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        location.setSRID(AppConstant.SRID);
        List<Object[]> accounts = accountDAO.findAllWithin(distance, minAge, maxAge, gender, AppConstant.LIMIT,
                                                           account.getIndex() * AppConstant.LIMIT, location);

        String previousId = "";
        List<CardDTO> cardDTOs = new ArrayList<>();
        CardDTO cardDTO = new CardDTO();

        for (Object[] cAccount : accounts) {
            String id = cAccount[ColumnIndex.ACCOUNT_PROFILE_ID].toString();
            if (!previousId.equals(id)) {

                cardDTOs.add(cardDTO);
                previousId = id;

                String name = cAccount[ColumnIndex.ACCOUNT_PROFILE_NAME].toString();
                String about = cAccount[ColumnIndex.ACCOUNT_PROFILE_ABOUT].toString();
                int birthYear = Integer.parseInt(cAccount[ColumnIndex.ACCOUNT_PROFILE_BIRTH_YEAR].toString());
                int distanceBetween = (int) ((double) cAccount[ColumnIndex.ACCOUNT_PROFILE_DISTANCE]);

                cardDTO = new CardDTO(id, name, about, birthYear, distanceBetween);
            }
            cardDTO.getPhotos().add(cAccount[ColumnIndex.ACCOUNT_PROFILE_PHOTO_KEY].toString());
        }

        cardDTOs.add(cardDTO);
        cardDTOs.remove(0);
        return cardDTOs;
    }

}
