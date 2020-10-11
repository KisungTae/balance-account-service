package com.beeswork.balanceaccountservice.service.account;


import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.question.QuestionDAO;
import com.beeswork.balanceaccountservice.dto.account.*;
import com.beeswork.balanceaccountservice.dto.firebase.FCMTokenDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestionId;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
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
public class AccountServiceImpl extends BaseServiceImpl implements AccountService {


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
    @Transactional
    public void saveLocation(LocationDTO locationDTO) throws AccountNotFoundException {
        Account account = accountDAO.findById(UUID.fromString(locationDTO.getAccountId()));
        Point location = geometryFactory.createPoint(new Coordinate(locationDTO.getLongitude(), locationDTO.getLatitude()));
        account.setLocation(location);
        accountDAO.persist(account);
    }

    @Override
    @Transactional
    public void saveFCMToken(FCMTokenDTO fcmTokenDTO)
    throws AccountNotFoundException, AccountInvalidException {

        Account account = accountDAO.findById(UUID.fromString(fcmTokenDTO.getAccountId()));
        if (!account.getEmail().equals(fcmTokenDTO.getEmail()))
            throw new AccountInvalidException();

        account.setFcmToken(fcmTokenDTO.getToken());
        accountDAO.persist(account);
    }




    @Override
    @Transactional
    public void save(AccountDTO accountDTO) throws AccountNotFoundException, QuestionNotFoundException {

        Account account = accountDAO.findById(UUID.fromString(accountDTO.getId()));

        account.setName(accountDTO.getName());
        account.setAbout(accountDTO.getAbout());

//        modelMapper.map(accountDTO, account);


//        saveQuestions(accountDTO.getAccountQuestionDTOs(), account);
        accountDAO.persist(account);

    }

    //  DESC 1. when registering, an account will be created with enabled = false, then when finish profiles,
    //          it will update enabled = true because users might get cards for which profile has not been updated
    //  TEST 2. save account without any changes but changes in accountQuestions --> Hibernate does not publish update DML for unchanged account even if you change accountQuestions
    //  TEST 3. save account without accountQuestions with modemapper --> modelmapper call setAccountQuestions and Hibernate recognizes this call and
    //          update persistence context which will delete all accountQuestions. Without modelmapper and update account fields only
    //          then Hibernate won't delete accountQuestions even if their size = 0
    @Override
    @Transactional
    public void saveProfile(AccountDTO accountDTO) throws AccountNotFoundException {

        Account account = accountDAO.findById(UUID.fromString(accountDTO.getId()));
        modelMapper.map(accountDTO, account);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(account.getBirth());
        account.setBirthYear(calendar.get(Calendar.YEAR));
        account.setUpdatedAt(new Date());
        account.setEnabled(true);
        accountDAO.persist(account);
    }

    //  TEST 1. save accountQuestionDTOs without setAccount() and setQuestion() --> Hibernate does not insert those objects, no exception thrown
    //  TEST 2. create new accountQuestionDTO with the same AccountQuestionId --> Hibernate throws exception of creating two object with the same Id
    //  DESC 3. I don't need to put accountQuestionDTO or accountQuestion because Hibernate anyway need a whole list of
    //          accountQuestions to check if it needs to remove or insert or update entities
    @Override
    @Transactional
    public void saveQuestions(AccountQuestionSaveDTO accountQuestionSaveDTO)
    throws QuestionNotFoundException, AccountNotFoundException {

        List<AccountQuestionDTO> accountQuestionDTOs = accountQuestionSaveDTO.getAccountQuestionDTOs();

        Account account = accountDAO.findByIdWithAccountQuestions(accountQuestionSaveDTO.getAccountId());
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
    @Transactional
    public void changeSwipeCount(String accountId, int count) throws AccountNotFoundException, InterruptedException {
        Account account = accountDAO.findById(UUID.fromString(accountId));
//        account.setSwipedCount(count);

        Account account1 = accountDAO.findById(UUID.fromString("619419af-e0d7-49e8-b8bf-f5b1fd6c60fe"));


        account.getSwipes().add(new Swipe(account, account1, false, new Date(), new Date()));

        System.out.println("thread sleep start");
        Thread.sleep(10000);
        System.out.println("thread sleep end");

        accountDAO.persist(account);
    }

    @Override
    @Transactional
    public void changeAbout(String accountId, String about) throws AccountNotFoundException {
        Account account = accountDAO.findById(UUID.fromString(accountId));
        account.setAbout(about);
        accountDAO.persist(account);
    }

}
