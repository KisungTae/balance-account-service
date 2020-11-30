package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.entity.account.*;
import com.beeswork.balanceaccountservice.entity.chat.Chat;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.match.MatchId;
import com.beeswork.balanceaccountservice.entity.photo.Photo;
import com.beeswork.balanceaccountservice.entity.photo.PhotoId;
import com.beeswork.balanceaccountservice.entity.question.QQuestion;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.service.firebase.FirebaseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.hibernate.Session;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
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


    @PersistenceContext
    private EntityManager entityManager;

    private final MatchDAO matchDAO;
    private final ObjectMapper objectMapper;
    private final FirebaseService firebaseService;
    private final AccountDAO accountDAO;
    private final ChatDAO chatDAO;

    @Autowired
    public DummyController(MatchDAO matchDAO, ObjectMapper objectMapper, FirebaseService firebaseService, AccountDAO accountDAO, ChatDAO chatDAO) {
        this.matchDAO = matchDAO;
        this.objectMapper = objectMapper;
        this.firebaseService = firebaseService;
        this.accountDAO = accountDAO;
        this.chatDAO = chatDAO;
    }


    @Transactional
    @GetMapping("question-by-account-id")
    public ResponseEntity<String> getQuestionsByAccountId(@RequestParam long accountId) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString("tuples"));
    }

    @Transactional
    @PostMapping("/create/swipe")
    public void createDummySwipe() throws InterruptedException {
        List<Account> accounts = new JPAQueryFactory(entityManager).selectFrom(QAccount.account).fetch();

        Random random = new Random();

        for (int i = 0; i < accounts.size(); i++) {
            Account swiper = accounts.get(i);

            int index1 = random.nextInt(accounts.size() - 1);
            int index2 = random.nextInt(accounts.size() - 1);

            int startIndex = Math.min(index1, index2);
            int endIndex = Math.max(index1, index2);

            for (int j = startIndex; j < endIndex; j++) {
                if (i != j) {
                    int count = random.nextInt(4);

                    for (int k = 0; k < count; k++) {
                        Swipe swipe = new Swipe(swiper, accounts.get(j), false, new Date(), new Date());
                        Thread.sleep(5);
                        swiper.getSwipes().add(swipe);
                    }

                    Swipe swipe = new Swipe(swiper, accounts.get(j), random.nextBoolean(), new Date(), new Date());
                    Thread.sleep(5);
                    swiper.getSwipes().add(swipe);
                }
            }
            entityManager.persist(swiper);
        }
    }

    @Transactional
    @PostMapping("/create/match")
    public void createDummyMatch() throws InterruptedException {


        List<Swipe> swipes = entityManager.unwrap(Session.class).createQuery("select s1 from Swipe s1 " +
                                                                             "inner join Swipe s2 on s1.swipedId = s2.swiperId " +
                                                                             "where s1.clicked = true and s2.clicked = true " +
                                                                             "and s1.swiperId = s2.swipedId order by s1.swiperId",
                                                                             Swipe.class).getResultList();

        for (Swipe swipe : swipes) {

            Thread.sleep(12);
            Chat chat = new Chat();

            Match newMatch = new Match();
            newMatch.setChat(chat);
            newMatch.setMatcher(swipe.getSwiper());
            newMatch.setMatched(swipe.getSwiped());
            newMatch.setUnmatcher(false);
            newMatch.setCreatedAt(new Date());
            newMatch.setUpdatedAt(new Date());
            newMatch.setMatchId(new MatchId(swipe.getSwiperId(), swipe.getSwipedId()));
            chatDAO.persist(chat);
            matchDAO.persist(newMatch);
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
            for (int k = 1; k <= 3; k++) {
                Question question = questions.get(index + k);
                AccountQuestion accountQuestion = new AccountQuestion();
                accountQuestion.setAccount(account);
                accountQuestion.setQuestion(question);
                accountQuestion.setAccountQuestionId(new AccountQuestionId(account.getId(), question.getId()));
                accountQuestion.setSelected(true);
                accountQuestion.setAnswer(random.nextBoolean());
                accountQuestion.setSequence(k);
                accountQuestion.setCreatedAt(new Date());
                accountQuestion.setUpdatedAt(new Date());
                account.getAccountQuestions().add(accountQuestion);
            }
        }
    }

    @Transactional
    @PostMapping("/create/questions")
    public void createDummyQuestions(@RequestParam int size) {

        for (int i = 1; i <= size; i++) {
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
    public void createDummyAccounts(@RequestParam int size) throws ParseException, InterruptedException {

        double startLat = 37.463557;
        double endLat = 37.650017;
        double startLon = 126.807883;
        double endLon = 127.176639;

        Random random = new Random();
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd");
        GeometryFactory gf = new GeometryFactory();
//        AccountType accountType = new JPAQueryFactory(entityManager).selectFrom(QAccountType.accountType).fetchFirst();

        int count = 1;

        Calendar calendar = Calendar.getInstance();

        for (double lon = startLon; lon <= endLon; lon += 0.000300) {

            for (double lat = startLat; lat <= endLat; lat += 0.000300) {

                boolean gender = random.nextInt(2) == 0;

                int year = random.nextInt((2003 - 1970)) + 1970;
                int month = random.nextInt((12 - 1)) + 1;
                int day = random.nextInt((25 - 1)) + 1;

                String birthString = String.valueOf(year) +
                                     (month < 10 ? "0" + month : month) +
                                     (day < 10 ? "0" + day : day);
                Date birth = originalFormat.parse(birthString);
                calendar.setTime(birth);

                Point location = gf.createPoint(new Coordinate(lon, lat));

                Account account = new Account();

//              Blocked and Enabled for integration test
                account.setBlocked(size == count);
                account.setIdentityToken(UUID.randomUUID());
                account.setEnabled(random.nextBoolean());
                account.setName(String.valueOf(count));
                account.setEmail(count + "@gmail.com");
                account.setAbout(count + ": this is my profile");
                account.setBirthYear(calendar.get(Calendar.YEAR));
                account.setBirth(birth);
                account.setGender(gender);
                account.setLocation(location);
                account.setAccountType(com.beeswork.balanceaccountservice.constant.AccountType.values()[random.nextInt(4)]);
                account.setScore(0);
                account.setPoint(50000);
                account.setFcmToken("");
                account.setCreatedAt(new Date());
                account.setUpdatedAt(new Date());
                account.setRepPhotoKeyUpdatedAt(new Date());

                if (random.nextBoolean())
                    account.setHeight(random.nextInt(50) + 150);

                for (int p = 0; p < 5; p++) {
                    Photo photo = new Photo();
                    Thread.sleep(12);
                    String photoKey = new Date().toInstant().toString();
                    photo.setPhotoId(new PhotoId(account.getId(), photoKey));

                    if (p == 0) {
                        account.setRepPhotoKey(photoKey);
                    }
                    photo.setSequence((long) count);
                    photo.setAccount(account);
                    account.getPhotos().add(photo);
                }
                entityManager.persist(account);
                count++;
                if (count > size) break;
            }
            if (count > size) break;
        }
        entityManager.flush();
    }

    @GetMapping("/send/notification/clicked")
    public void sendDummyClickedNotification(@RequestParam("clickedId") String clickedId)
    throws AccountNotFoundException, FirebaseMessagingException {

//        Account clicked = accountDAO.findById(UUID.fromString(clickedId));
//
//        FCMNotificationDTO notificationDTO = FCMNotificationDTO.clickedNotification(clicked.getFcmToken(),
//                                                                                    clicked.getRepPhotoKey());
//        List<FCMNotificationDTO> notificationDTOs = new ArrayList<>();
//        notificationDTOs.add(notificationDTO);
//        fcmService.sendNotifications(notificationDTOs);
    }

    @GetMapping("/send/notification/match")
    public void sendDummyMatchNotification(@RequestParam("matcherId") String matcherId,
                                           @RequestParam("matchedId") String matchedId)
    throws AccountNotFoundException, FirebaseMessagingException {

//        Account matcher = accountDAO.findById(UUID.fromString(matcherId));
//        Account matched = accountDAO.findById(UUID.fromString(matchedId));
//
//        FCMNotificationDTO matcherNotification = FCMNotificationDTO.matchNotification(matcher.getFcmToken(),
//                                                                                      matcher.getRepPhotoKey());
//
//        FCMNotificationDTO matchedNotification = FCMNotificationDTO.matchNotification(matched.getFcmToken(),
//                                                                                      matched.getRepPhotoKey());
//
//        List<FCMNotificationDTO> notificationDTOs = new ArrayList<>();
//        notificationDTOs.add(matcherNotification);
//        notificationDTOs.add(matchedNotification);
//
//        fcmService.sendNotifications(notificationDTOs);

    }


//    @PostMapping("/change/swipe-count")
//    public void changeSwipeCount(@RequestParam("count") int count,
//                                 @RequestParam("accountId") String accountId)
//    throws AccountNotFoundException, InterruptedException {
//        try {
//            accountService.changeSwipeCount(accountId, count);
//        } catch (ObjectOptimisticLockingFailureException exception) {
//            System.out.println("ObjectOptimisticLockingFailureException in account controller");
//            System.out.println("exception message: " + exception.getMessage());
//        }
//    }
//
//    @PostMapping("/change/about")
//    public void changeAbout(@RequestParam("about") String about,
//                            @RequestParam("accountId") String accountId) throws AccountNotFoundException {
//
//        accountService.changeAbout(accountId, about);
//
//    }

}
