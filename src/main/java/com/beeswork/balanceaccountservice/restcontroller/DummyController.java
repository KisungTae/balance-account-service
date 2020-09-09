package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.config.properties.AWSProperties;
import com.beeswork.balanceaccountservice.dao.accounttype.AccountTypeDAO;
import com.beeswork.balanceaccountservice.entity.QAccountType;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.AccountType;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class DummyController {

    @Autowired
    private AWSProperties awsProperties;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AccountTypeDAO accountTypeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/test")
    @Transactional
    public String test() {


//        AccountQuestionRel accountQuestionRel = new AccountQuestionRel();
//        accountQuestionRel.setAccountQuestionRelId(new AccountQuestionRelId(account.getId(), question.getId()));
//        accountQuestionRel.setEnabled(true);
//        accountQuestionRel.setSelected(true);
//        accountQuestionRel.setCreated_at(new Date());
//        accountQuestionRel.setUpdated_at(new Date());

//        entityManager.persist(accountQuestionRel);


//        createDummyAccountTypes(5);
//        createDummyAccounts(50);
//        createDummyQuestions(50);
//        createDummyAccountQuestionRel(151, 151, false, 3);
        return messageSource.getMessage("test.notfound", null, Locale.getDefault());
    }


    @Transactional
    @GetMapping("question-by-account-id")
    public ResponseEntity<String> getQuestionsByAccountId(@RequestParam long accountId) throws JsonProcessingException {
//        QAccountQuestion qAccountQuestion = QAccountQuestion.accountQuestion;
//        QAccount qAccount = QAccount.account;
//        QQuestion qQuestion = QQuestion.question;
//
//        List<Tuple> tuples = new JPAQueryFactory(entityManager).select(qAccountQuestion.account.id,
//                                                                       qAccountQuestion.question.id,
//                                                                       qQuestion.description,
//                                                                       qQuestion.topOption,
//                                                                       qQuestion.bottomOption,
//                                                                       qAccountQuestion.enabled,
//                                                                       qAccountQuestion.selected)
//                                                               .from(qAccountQuestion)
//                                                               .innerJoin(qAccountQuestion.question, qQuestion)
//                                                               .where(qAccountQuestion.account.id.eq(accountId))
//                                                               .fetch();


        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString("tuples"));
    }

    @Transactional
    @PostMapping("/create/dummy-account-question")
    public void createDummyAccountQuestion(@RequestParam long accountId,
                                           @RequestParam long questionId,
                                           @RequestParam boolean selected,
                                           @RequestParam int sequence) {

//        Account account = new JPAQueryFactory(entityManager).selectFrom(QAccount.account)
//                                                            .where(QAccount.account.id.eq(accountId))
//                                                            .fetchOne();
//
//        Question question = new JPAQueryFactory(entityManager).selectFrom(QQuestion.question)
//                                                              .where(QQuestion.question.id.eq(questionId))
//                                                              .fetchOne();
//
//        AccountQuestion accountQuestion = new AccountQuestion();
//        accountQuestion.setAccountQuestionId(new AccountQuestionId(account.getId(), question.getId()));
//        accountQuestion.setAccount(account);
//        accountQuestion.setQuestion(question);
//        accountQuestion.setSequence(sequence);
//        accountQuestion.setEnabled(true);
//        accountQuestion.setSelected(selected);
//        accountQuestion.setCreatedAt(new Date());
//        accountQuestion.setUpdatedAt(new Date());
//
//        account.getAccountQuestions().add(accountQuestion);
//        entityManager.persist(account);
//        entityManager.persist(accountQuestion);
    }

    @Transactional
    @PostMapping("/create/dummy-account-types")
    public void createDummyAccountTypes(@RequestParam int size) {
        for (int i = 0; i < size; i++) {
            AccountType accountType = new AccountType();
            accountType.setDescription("login type - " + i);
            entityManager.persist(accountType);
        }
        entityManager.flush();
    }

    @Transactional
    @PostMapping("/create/dummy-questions")
    public void createDummyQuestions(@RequestParam int size) {
        for (int i = 0; i < size; i++) {
            Question question = new Question();
            question.setDescription("question-" + i);
            question.setTopOption("question-" + i + " top option");
            question.setBottomOption("question-" + i + " bottom option");
            question.setCreatedAt(new Date());
            question.setUpdatedAt(new Date());
            entityManager.persist(question);
        }
        entityManager.flush();
    }

    @Transactional
    @PostMapping("/create/dummy-accounts")
    public void createDummyAccounts(@RequestParam int size) {

        Random random = new Random();

        GeometryFactory gf = new GeometryFactory();
        Point location = gf.createPoint(new Coordinate(126.807883, 37.463557));

        AccountType accountType = new JPAQueryFactory(entityManager).selectFrom(QAccountType.accountType).fetchFirst();

        for (int i = 0; i < size; i++) {

            int birth = random.nextInt(50) + 1950;
            boolean gender = random.nextInt(2) == 0;
            int score = random.nextInt(1000);
            int index = random.nextInt(1000);
            int point = random.nextInt(1000);

            Account account = new Account();
            account.setBlocked(false);
            account.setName("account-" + i);
            account.setEmail("account-" + i + "@gmail.com");
//            account.setBirth(birth);
            account.setGender(gender);
            account.setAbout("account-" + i + " about");
            account.setScore(score);
            account.setIndex(index);
            account.setPoint(point);
            account.setFavorCount(0);
            account.setFavorCountUpdatedAt(new Date());
            account.setLocation(location);
            account.setAccountType(accountType);
            account.setCreatedAt(new Date());
            account.setUpdatedAt(new Date());
            entityManager.persist(account);
        }
        entityManager.flush();

    }
}
