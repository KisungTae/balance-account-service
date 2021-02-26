package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.constant.LoginType;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.entity.account.*;
import com.beeswork.balanceaccountservice.entity.chat.Chat;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.match.MatchId;
import com.beeswork.balanceaccountservice.entity.match.QMatch;
import com.beeswork.balanceaccountservice.entity.photo.Photo;
import com.beeswork.balanceaccountservice.entity.photo.PhotoId;
import com.beeswork.balanceaccountservice.entity.profile.Profile;
import com.beeswork.balanceaccountservice.entity.question.QQuestion;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.entity.swipe.SwipeId;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.service.firebase.FirebaseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Session;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
    public DummyController(MatchDAO matchDAO,
                           ObjectMapper objectMapper,
                           FirebaseService firebaseService,
                           AccountDAO accountDAO,
                           ChatDAO chatDAO) {
        this.matchDAO = matchDAO;
        this.objectMapper = objectMapper;
        this.firebaseService = firebaseService;
        this.accountDAO = accountDAO;
        this.chatDAO = chatDAO;
    }

    @Transactional
    @PostMapping("/create/accounts")
    public void createDummyAccounts(@RequestParam int size) throws InterruptedException {
        double startLat = 37.463557;
        double endLat = 37.650017;
        double startLon = 126.807883;
        double endLon = 127.176639;

        Random random = new Random();
        GeometryFactory gf = new GeometryFactory();

        int count = 1;

        for (double lon = startLon; lon <= endLon; lon += 0.000300) {
            for (double lat = startLat; lat <= endLat; lat += 0.000300) {
                saveAccount(lat, lon, count, random, gf);
                count++;
                if (count > size) break;
            }
            if (count > size) break;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveAccount(double lat, double lon, int count, Random random, GeometryFactory geometryFactory) {
        Account account = new Account();
        Date birth = randomBirth();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(birth);
        Date now = new Date();

        String name = "user-" + count;
        account.setIdentityToken(UUID.randomUUID());
        account.setName(name);
        account.setEmail(count + "@gmail.com");
        account.setAbout(count + ": this is my profile");
        account.setBirthYear(calendar.get(Calendar.YEAR));
        account.setBirth(birth);
        account.setGender(random.nextBoolean());
        account.setLocation(geometryFactory.createPoint(new Coordinate(lon, lat)));
        account.setLoginType(LoginType.values()[random.nextInt(4)]);
        account.setScore(0);
        account.setPoint(50000);
        account.setFcmToken("");
        account.setFreeSwipe(2000);
        account.setFreeSwipeUpdatedAt(now);
        account.setCreatedAt(now);
        account.setUpdatedAt(now);
        if (random.nextBoolean()) account.setHeight(random.nextInt(50) + 150);


        // profile
        Profile profile = new Profile(account,
                                      name,
                                      calendar.get(Calendar.YEAR),
                                      birth,
                                      random.nextBoolean(),
                                      random.nextInt(50) + 150,
                                      count + ": this is my profile",
                                      geometryFactory.createPoint(new Coordinate(lon, lat)),
                                      now);


        // photo
        Date photoKeyDate = now;
        for (int p = 0; p < 5; p++) {
            Photo photo = new Photo();
            photoKeyDate = DateUtils.addMinutes(photoKeyDate, 1);
            photo.setPhotoId(new PhotoId(account.getId(), photoKeyDate.toString()));

            if (p == 0) {
                account.setRepPhotoKey(photoKeyDate.toString());
                account.setUpdatedAt(photoKeyDate);
            }
            photo.setSequence(count);
            photo.setAccount(account);
            account.getPhotos().add(photo);
        }

        entityManager.persist(account);
        entityManager.persist(profile);
    }

    private Date randomBirth() {
        Random random = new Random();
        int year = random.nextInt((2003 - 1970)) + 1970;
        int month = random.nextInt((12 - 1));
        int day = random.nextInt((25 - 1)) + 1;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0);
        return calendar.getTime();
    }


    @Transactional
    @GetMapping("question-by-account-id")
    public ResponseEntity<String> getQuestionsByAccountId(@RequestParam long accountId) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString("tuples"));
    }

    @Transactional
    @PostMapping("/create/swipe")
    public void createDummySwipe() {
        List<Account> accounts = new JPAQueryFactory(entityManager).selectFrom(QAccount.account).fetch();
        Random random = new Random();
        int maxOffset = 20;
        for (int i = 0; i < accounts.size(); i++) {
            int index1 = random.nextInt(accounts.size() - 1);
            int index2 = random.nextInt(accounts.size() - 1);

            int startIndex = Math.min(index1, index2);
            int endIndex = Math.max(index1, index2);

            int distance = endIndex - startIndex;
            if (distance > maxOffset) endIndex = startIndex + maxOffset;

            saveSwipe(accounts, i, startIndex, endIndex, random);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSwipe(List<Account> accounts, int i, int startIndex, int endIndex, Random random) {
        Account swiper = accounts.get(i);
        for (int j = startIndex; j < endIndex; j++) {
            if (i == j) continue;
            Date now = new Date();
            Swipe swipe = new Swipe();
            Account swiped = accounts.get(j);
            swipe.setSwipeId(new SwipeId(swiper.getId(), swiped.getId()));
            swipe.setSwiper(swiper);
            swipe.setSwiped(swiped);
            swipe.setClicked(random.nextBoolean());
            swipe.setCount((random.nextInt(10) + 1));
            swipe.setCreatedAt(now);
            swipe.setUpdatedAt(now);
            swiper.getSwipes().add(swipe);
        }
        entityManager.persist(swiper);
    }

    @PostMapping("/update/account-and-match")
    public void updateAccountAndMatch() throws InterruptedException {
        List<Account> accounts = new JPAQueryFactory(entityManager).selectFrom(QAccount.account).fetch();
        Random random = new Random();
        for (Account account : accounts) {
            if (random.nextBoolean()) {
                account.setUpdatedAt(new Date());
                Thread.sleep(10);
            } else if (account.getMatches().size() > 0) {
                Match match = account.getMatches().get(0);
                List<Match> matches = new JPAQueryFactory(entityManager).selectFrom(QMatch.match)
                                                                        .where(QMatch.match.chatId.eq(match.getChatId()))
                                                                        .fetch();
                Date date = new Date();
                for (Match match1 : matches) {
                    match1.setUpdatedAt(date);
                }
            }
        }
    }

    @PostMapping("/update/rep-photo-updated-at")
    public void updateMatchUpdatedAt() {

    }


    @Transactional
    @PostMapping("/create/match")
    public void createDummyMatch() throws InterruptedException {


        List<Swipe> swipes = entityManager.unwrap(Session.class).createQuery("select s1 from Swipe s1 " +
                                                                             "inner join Swipe s2 on s1.swipedId = s2.swiperId " +
                                                                             "where s1.clicked = true and s2.clicked = true " +
                                                                             "and s1.swiperId = s2.swipedId order by s1.swiperId",
                                                                             Swipe.class).getResultList();
        HashMap<String, Match> matchMap = new HashMap<>();

        for (Swipe swipe : swipes) {

            Thread.sleep(10);
            Date date = new Date();
            Chat chat = new Chat();

            MatchId theOtherPartyMatchId = new MatchId(swipe.getSwipedId(), swipe.getSwiperId());
            if (matchMap.containsKey(swipe.getSwipedId().toString() + swipe.getSwiperId().toString())) {
                chat = matchMap.get(swipe.getSwipedId().toString() + swipe.getSwiperId().toString()).getChat();
            }
            swipe.setMatched(true);

            Match newMatch = new Match();
            newMatch.setChat(chat);
            newMatch.setMatcher(swipe.getSwiper());
            newMatch.setMatched(swipe.getSwiped());
            newMatch.setUnmatcher(false);

            newMatch.setCreatedAt(date);
            newMatch.setUpdatedAt(date);

            MatchId matchId = new MatchId(swipe.getSwiperId(), swipe.getSwipedId());
            newMatch.setMatchId(matchId);

            chatDAO.persist(chat);
            matchDAO.persist(newMatch);

            matchMap.put(swipe.getSwiperId().toString() + swipe.getSwipedId().toString(), newMatch);
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
        System.out.println(Thread.currentThread().getName() + ": createDummyQuestions");
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
    @PostMapping("/create/chat-messages")
    public void createDummyChatMessages() {
        List<Match> matches = new JPAQueryFactory(entityManager).selectFrom(QMatch.match).fetch();
        Date date = new Date();
        Random random = new Random();
        int innerCount = 0;
        for (int i = 0; i < 20; i++) {
            date = DateUtils.addSeconds(date, 30);


            for (Match match : matches) {
                if (random.nextBoolean()) {
                    innerCount++;
                    boolean who = random.nextBoolean();
                    Account sender = who ? match.getMatcher() : match.getMatched();
                    Account receiver = who ? match.getMatched() : match.getMatcher();
                    match.getChat()
                         .getChatMessages()
                         .add(new ChatMessage(match.getChat(),
                                              sender,
                                              receiver,
                                              (long) i + innerCount,
                                              "message-" + random.nextFloat(),
                                              date));
                }
            }
        }


//        Random random = new Random();
//        for (Match match : matches) {
//            int messageCount = random.nextInt(20);
//            Chat chat = match.getChat();
//            List<ChatMessage> chatMessages = match.getChat().getChatMessages();
//            for (int i = 0; i < messageCount; i++) {
//                int matcherCount = random.nextInt(5);
//                int matchedCount = random.nextInt(5);
//
//                for (int j = 0; j < matcherCount; j++) {
//                    Long messageId = (long) (i + j);
//                    chatMessages.add(new ChatMessage(chat,
//                                                     match.getMatcher(),
//                                                     match.getMatched(),
//                                                     messageId,
//                                                     "message body" + i + j,
//                                                     new Date()));
//                    Thread.sleep(10);
//                }
//                for (int k = 0; k < matchedCount; k++) {
//                    Long messageId = (long) (i + k);
//                    Date newDate = new Date();
//                    chatMessages.add(new ChatMessage(chat,
//                                                     match.getMatched(),
//                                                     match.getMatcher(),
//                                                     messageId,
//                                                     "message body" + i + k,
//                                                     newDate));
//                    Thread.sleep(10);
//                    if (i == messageCount - 1 && k == matchedCount - 1) {
//                        chat.setUpdatedAt(newDate);
//                    }
//                }
//
//            }
//        }
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
