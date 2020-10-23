package com.beeswork.balanceaccountservice.service.account;


import com.beeswork.balanceaccountservice.constant.AppConstant;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.question.QuestionDAO;
import com.beeswork.balanceaccountservice.dto.account.*;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.exception.account.AccountBlockedException;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.question.QuestionNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class AccountServiceImpl extends BaseServiceImpl implements AccountService, AccountInterService {


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
    public Account findValid(UUID accountId, String email) {
        Account account = accountDAO.findById(accountId);
        checkIfValid(account, email);
        return account;
    }

    @Override
    public void checkIfValid(UUID accountId, String email) {
        Account account = accountDAO.findById(accountId);
        checkIfValid(account, email);
    }

    @Override
    public void checkIfValid(Account account, String email) {
        checkIfBlocked(account);
        if (!account.getEmail().equals(email)) throw new AccountInvalidException();
    }

    @Override
    public void checkIfBlocked(Account account) {
        if (account == null) throw new AccountNotFoundException();
        if (account.isBlocked()) throw new AccountBlockedException();
    }

    @Override
    @Transactional
    public void save(AccountDTO accountDTO) {

//      TODO: create account with email

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
        Account account = findValid(UUID.fromString(accountId), email);

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
        Account account = findValid(UUID.fromString(accountId), email);
        account.setLocation(geometryFactory.createPoint(new Coordinate(latitude, longitude)));
    }

    @Override
    @Transactional
    public void saveFCMToken(String accountId, String email, String token) {
        Account account = findValid(UUID.fromString(accountId), email);
        account.setFcmToken(token);
    }

    //  TEST 1. save accountQuestionDTOs without setAccount() and setQuestion() --> Hibernate does not insert those objects, no exception thrown
    //  TEST 2. create new accountQuestionDTO with the same AccountQuestionId --> Hibernate throws exception of creating two object with the same Id
    //  DESC 3. I don't need to put accountQuestionDTO or accountQuestion because Hibernate anyway need a whole list of
    //          accountQuestions to check if it needs to remove or insert or update entities
    @Override
    @Transactional
    public void saveQuestions(String accountId, String email, List<AccountQuestionDTO> accountQuestionDTOs) {
        Account account = accountDAO.findByIdWithAccountQuestions(UUID.fromString(accountId));
        checkIfValid(account, email);
        account.getAccountQuestions().clear();

        for (AccountQuestionDTO accountQuestionDTO : accountQuestionDTOs) {
            Question question = questionDAO.findById(accountQuestionDTO.getQuestionId());
            if (question == null) throw new QuestionNotFoundException();

            Date date = new Date();
            account.getAccountQuestions().add(new AccountQuestion(account,
                                                                  question,
                                                                  accountQuestionDTO.isSelected(),
                                                                  accountQuestionDTO.getSequence(),
                                                                  date,
                                                                  date));
        }
    }

    @Override
    public List<Object[]> findAllWithin(UUID accountId, String email, int distance, int minAge, int maxAge, boolean gender, double latitude, double longitude) {
        Account account = findValid(accountId, email);
        Point location = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        location.setSRID(AppConstant.SRID);
        return accountDAO.findAllWithin(accountId, distance, minAge, maxAge, gender, AppConstant.LIMIT,
                                        AppConstant.LIMIT * account.getIndex(), location);
    }

    @Override
    public Account findWithQuestions(UUID accountId) {
        Account account = accountDAO.findByIdWithQuestions(accountId);
        checkIfBlocked(account);
        return account;
    }

    @Override
    public void persist(Account account) {
        accountDAO.persist(account);
    }
}
