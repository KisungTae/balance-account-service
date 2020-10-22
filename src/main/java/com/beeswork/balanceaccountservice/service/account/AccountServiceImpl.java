package com.beeswork.balanceaccountservice.service.account;


import com.beeswork.balanceaccountservice.constant.AppConstant;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.question.QuestionDAO;
import com.beeswork.balanceaccountservice.dto.account.*;
import com.beeswork.balanceaccountservice.dto.firebase.FCMTokenDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestionId;
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
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl extends BaseServiceImpl implements AccountService, AccountInterService {


    private final AccountDAO accountDAO;
    private final QuestionDAO questionDAO;

    private final GeometryFactory geometryFactory;

    @Autowired
    public AccountServiceImpl(AccountDAO accountDAO,
                              ModelMapper modelMapper,
                              QuestionDAO questionDAO,
                              GeometryFactory geometryFactory) {
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

    //  DESC 1. when registering, an account will be created with enabled = false, then when finish profiles,
    //          it will update enabled = true because users might get cards for which profile has not been updated
    //  TEST 2. save account without any changes but changes in accountQuestions --> Hibernate does not publish update DML for unchanged account even if you change accountQuestions
    //  TEST 3. save account without accountQuestions with modemapper --> modelmapper call setAccountQuestions and Hibernate recognizes this call and
    //          update persistence context which will delete all accountQuestions. Without modelmapper and update account fields only
    //          then Hibernate won't delete accountQuestions even if their size = 0
    @Override
    @Transactional
    public void saveProfile(AccountDTO accountDTO) {
        Account account = findValid(UUID.fromString(accountDTO.getId()), accountDTO.getEmail());

        if (!account.isEnabled()) {
            account.setName(accountDTO.getName());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(account.getBirth());

            account.setBirthYear(calendar.get(Calendar.YEAR));
            account.setBirth(accountDTO.getBirth());
            account.setGender(accountDTO.isGender());
        }

        account.setAbout(accountDTO.getAbout());
        account.setUpdatedAt(new Date());
        account.setEnabled(true);
        account.setLocation(geometryFactory.createPoint(new Coordinate(accountDTO.getLongitude(), accountDTO.getLatitude())));

//        accountDAO.persist(account);
    }

    @Override
    @Transactional
    public void saveLocation(LocationDTO locationDTO) {
        Account account = findValid(UUID.fromString(locationDTO.getAccountId()), locationDTO.getEmail());
        account.setLocation(geometryFactory.createPoint(new Coordinate(locationDTO.getLongitude(), locationDTO.getLatitude())));
        accountDAO.persist(account);
    }

    @Override
    @Transactional
    public void saveFCMToken(FCMTokenDTO fcmTokenDTO) {
        Account account = findValid(UUID.fromString(fcmTokenDTO.getAccountId()), fcmTokenDTO.getEmail());
        account.setFcmToken(fcmTokenDTO.getToken());
        accountDAO.persist(account);
    }

    @Override
    @Transactional
    public void save(AccountDTO accountDTO) {

//      TODO: create account with email

    }




    //  TEST 1. save accountQuestionDTOs without setAccount() and setQuestion() --> Hibernate does not insert those objects, no exception thrown
    //  TEST 2. create new accountQuestionDTO with the same AccountQuestionId --> Hibernate throws exception of creating two object with the same Id
    //  DESC 3. I don't need to put accountQuestionDTO or accountQuestion because Hibernate anyway need a whole list of
    //          accountQuestions to check if it needs to remove or insert or update entities
    @Override
    @Transactional
    public void saveQuestions(AccountQuestionSaveDTO accountQuestionSaveDTO) {

        List<AccountQuestionDTO> accountQuestionDTOs = accountQuestionSaveDTO.getAccountQuestionDTOs();

        Account account = accountDAO.findByIdWithAccountQuestions(accountQuestionSaveDTO.getAccountId());
        checkIfValid(account, accountQuestionSaveDTO.getEmail());

        List<AccountQuestion> accountQuestions = account.getAccountQuestions();

        for (int i = accountQuestions.size() - 1; i >= 0; i--) {
            AccountQuestion accountQuestion = accountQuestions.get(i);
            AccountQuestionDTO accountQuestionDTO = accountQuestionDTOs.stream()
                                                                       .filter(a -> a.getQuestionId() ==
                                                                               accountQuestion.getQuestionId())
                                                                       .findFirst()
                                                                       .orElse(null);

            if (accountQuestionDTO == null) accountQuestions.remove(accountQuestion);
            else {
                modelMapper.map(accountQuestionDTO, accountQuestion);
                accountQuestion.setUpdatedAt(new Date());
                accountQuestionDTOs.remove(accountQuestionDTO);
            }
        }


        List<Long> questionIds = accountQuestionDTOs.stream()
                                                    .map(AccountQuestionDTO::getQuestionId)
                                                    .collect(Collectors.toList());

        List<Question> questions = questionDAO.findAllByIds(questionIds);

        for (int i = accountQuestionDTOs.size() - 1; i >= 0; i--) {
            AccountQuestionDTO accountQuestionDTO = accountQuestionDTOs.get(i);
            Question question = questions.stream()
                                         .filter(q -> q.getId() == accountQuestionDTO.getQuestionId())
                                         .findFirst()
                                         .orElseThrow(QuestionNotFoundException::new);

            AccountQuestion accountQuestion = modelMapper.map(accountQuestionDTO, AccountQuestion.class);
            accountQuestion.setAccountQuestionId(new AccountQuestionId(account.getId(), question.getId()));
            accountQuestion.setAccount(account);
            accountQuestion.setQuestion(question);
            accountQuestion.setCreatedAt(new Date());
            accountQuestion.setUpdatedAt(new Date());
            accountQuestions.add(accountQuestion);
            accountQuestionDTOs.remove(accountQuestionDTO);
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
