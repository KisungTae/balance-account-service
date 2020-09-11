package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.config.properties.AWSProperties;
import com.beeswork.balanceaccountservice.dao.accounttype.AccountTypeDAO;
import com.beeswork.balanceaccountservice.entity.account.*;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.match.MatchId;
import com.beeswork.balanceaccountservice.entity.question.QQuestion;
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
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/dummy")
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
    @PostMapping("/create/match")
    public void createDummyMatch(@RequestParam int size) {
        List<Account> accounts = new JPAQueryFactory(entityManager).selectFrom(QAccount.account).fetch();

        int count = 0;

        Random random = new Random();
        for (Account account : accounts) {

            count++;

            int index =  random.nextInt((accounts.size() - 10));

            for (int i = 0;  i < 3; i++) {
                Account matched = accounts.get(index);

                Match match = account.getMatches().stream().filter(d -> d.getMatchedId() == matched.getId()).findFirst().orElse(null);
                if (match == null) {
                    Match newMatcher = new Match();
                    newMatcher.setMatcher(account);
                    newMatcher.setMatched(matched);
                    newMatcher.setCreatedAt(new Date());
                    newMatcher.setMatchId(new MatchId(account.getId(), matched.getId()));
                    account.getMatches().add(newMatcher);

                    Match newMatched = new Match();
                    newMatched.setMatcher(matched);
                    newMatched.setMatched(account);
                    newMatched.setCreatedAt(new Date());
                    newMatched.setMatchId(new MatchId(matched.getId(), account.getId()));
                    matched.getMatches().add(newMatched);
                }
            }

//            if (count > size) break;
        }
    }

    @Transactional
    @PostMapping("/create/account-question")
    public void createDummyAccountQuestion() {

        List<Account> accounts = new JPAQueryFactory(entityManager).selectFrom(QAccount.account).fetch();
        List<Question> questions = new JPAQueryFactory(entityManager).selectFrom(QQuestion.question).fetch();

        Random random = new Random();

        for (Account account : accounts) {
            int index = random.nextInt(questions.size() - 6);
            for (int k = 0; k < 3; k++) {
                Question question = questions.get(index + k);
                AccountQuestion accountQuestion = new AccountQuestion();
                accountQuestion.setAccount(account);
                accountQuestion.setQuestion(question);
                accountQuestion.setAccountQuestionId(new AccountQuestionId(account.getId(), question.getId()));
                accountQuestion.setSelected(random.nextBoolean());
                accountQuestion.setCreatedAt(new Date());
                accountQuestion.setUpdatedAt(new Date());
                account.getAccountQuestions().add(accountQuestion);
            }
        }
    }

    @Transactional
    @PostMapping("/create/account-types")
    public void createDummyAccountTypes(@RequestParam int size) {
        for (int i = 0; i < size; i++) {
            AccountType accountType = new AccountType();
            accountType.setDescription("login type - " + i);
            entityManager.persist(accountType);
        }
        entityManager.flush();
    }

    @Transactional
    @PostMapping("/create/questions")
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
    @PostMapping("/create/accounts")
    public void createDummyAccounts(@RequestParam int size) throws ParseException {

        double startLat = 37.463557;
        double endLat   = 37.650017;
        double startLon = 126.807883;
        double endLon   = 127.176639;

        Random random = new Random();
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd");
        GeometryFactory gf = new GeometryFactory();
        AccountType accountType = new JPAQueryFactory(entityManager).selectFrom(QAccountType.accountType).fetchFirst();


        int count = 0;
        int lonCount = 0;
        int latCount = 0;

        Calendar calendar = Calendar.getInstance();

        for (double lon = startLon; lon <= endLon; lon+= 0.000300) {

            for (double lat = startLat; lat <= endLat; lat += 0.000300) {

                boolean gender = random.nextInt(2) == 0;

                int year = random.nextInt((2003 - 1970)) + 1970;
                int month = random.nextInt((12 - 1)) + 1;
                int day = random.nextInt((25 - 1)) + 1;
                String birthString = String.valueOf(year) + (month < 10 ? "0" + month : month) + (day < 10 ? "0" + day : day);
                Date birth = originalFormat.parse(birthString);
                Point location = gf.createPoint(new Coordinate(lon, lat));
                String name = "account | " + lat + " | " + lon;
                calendar.setTime(birth);

                Account account = new Account();
                account.setBlocked(false);
                account.setEnabled(true);
                account.setName(name);
                account.setEmail("email | " + lat + " | " + lon);
                account.setAbout("this is about");
                account.setBirthYear(calendar.get(Calendar.YEAR));
                account.setBirth(birth);
                account.setGender(gender);
                account.setLocation(location);
                account.setAccountType(accountType);
                account.setScore(latCount);
                account.setPoint(lonCount);
                account.setLikedCountUpdatedAt(new Date());
                account.setCreatedAt(new Date());
                account.setUpdatedAt(new Date());


                for (int p = 0; p < 5; p++) {
                    Photo photo = new Photo();
                    photo.setAccount(account);
                    photo.setCreatedAt(new Date());
                    photo.setUpdatedAt(new Date());
                    photo.setSequence(p);
                    photo.setUrl("https://aws." + name);
                    account.getPhotos().add(photo);
                }


                entityManager.persist(account);

                count++;
                latCount++;
            }

            if (count > size) break;
            lonCount++;
        }

        entityManager.flush();
    }
}
