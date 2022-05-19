package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.config.security.JWTTokenProvider;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatMessageDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dao.setting.PushSettingDAO;
import com.beeswork.balanceaccountservice.dao.wallet.WalletDAO;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.chat.SaveChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import com.beeswork.balanceaccountservice.entity.account.*;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.match.QMatch;
import com.beeswork.balanceaccountservice.entity.photo.Photo;
import com.beeswork.balanceaccountservice.entity.photo.PhotoId;
import com.beeswork.balanceaccountservice.entity.profile.Profile;
import com.beeswork.balanceaccountservice.entity.question.QQuestion;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.entity.setting.PushSetting;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.service.chat.ChatService;
import com.beeswork.balanceaccountservice.service.fcm.FCMService;
import com.beeswork.balanceaccountservice.service.stomp.StompService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.tomcat.jni.Local;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/dummy")
public class DummyController {

    @PersistenceContext
    private EntityManager entityManager;

    private final MatchDAO         matchDAO;
    private final ObjectMapper     objectMapper;
    private final FCMService       fcmService;
    private final AccountDAO       accountDAO;
    private final WalletDAO        walletDAO;
    private final PushSettingDAO   pushSettingDAO;
    private final GeometryFactory  geometryFactory;
    private final StompService     stompService;
    private final ChatService      chatService;
    private final JWTTokenProvider jwtTokenProvider;
    private final JPQLQueryFactory jpqlQueryFactory;
    private final ChatMessageDAO chatMessageDAO;

    @Autowired
    public DummyController(MatchDAO matchDAO,
                           ObjectMapper objectMapper,
                           FCMService FCMService,
                           AccountDAO accountDAO,
                           WalletDAO walletDAO,
                           PushSettingDAO pushSettingDAO,
                           GeometryFactory geometryFactory,
                           StompService stompService,
                           ChatService chatService,
                           JWTTokenProvider jwtTokenProvider,
                           JPQLQueryFactory jpqlQueryFactory, ChatMessageDAO chatMessageDAO) {
        this.matchDAO = matchDAO;
        this.objectMapper = objectMapper;
        this.fcmService = FCMService;
        this.accountDAO = accountDAO;
        this.walletDAO = walletDAO;
        this.pushSettingDAO = pushSettingDAO;
        this.geometryFactory = geometryFactory;
        this.stompService = stompService;
        this.chatService = chatService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jpqlQueryFactory = jpqlQueryFactory;
        this.chatMessageDAO = chatMessageDAO;
    }


    @Transactional
    @PostMapping("/create/for-list-matches")
    public void createDummyForListMatches() {

    }




    @Transactional
    @PostMapping("/create/match-for-account")
    public void createDummyMatchForAccount(@RequestParam UUID accountId) {
        Account account = accountDAO.findById(accountId, false);

    }

    @Transactional
    @PostMapping("/create/chat-message")
    public void createDummyChatMessage(@RequestParam UUID swiperId,
                                       @RequestParam UUID swipedId,
                                       @RequestParam UUID chatId,
                                       @RequestParam int count) {
        Account swiper = accountDAO.findById(swiperId, false);
        Account swiped = accountDAO.findById(swipedId, false);
        Random random = new Random();
        Date date = new Date();

        Match swiperMatch = matchDAO.findBy(swiperId, swipedId, false);
        Match swipedMatch = matchDAO.findBy(swipedId, swiperId, false);

        for (int i = 0; i < count; i++) {
            date = DateUtils.addSeconds(date, 30);
            if (random.nextBoolean()) {
                ChatMessage chatMessage = new ChatMessage(swiper, chatId, "message-" + i, UUID.randomUUID(), date, date);
                chatMessageDAO.persist(chatMessage);
                swipedMatch.setLastReceivedChatMessageId(chatMessage.getId());
                swipedMatch.setLastChatMessageId(chatMessage.getId());
                swipedMatch.setLastChatMessageBody(chatMessage.getBody());
                swiperMatch.setLastChatMessageId(chatMessage.getId());
                swiperMatch.setLastChatMessageBody(chatMessage.getBody());
            } else {
                ChatMessage chatMessage = new ChatMessage(swiped, chatId, "message-" + i, UUID.randomUUID(), date, date);
                chatMessageDAO.persist(chatMessage);
                swiperMatch.setLastReceivedChatMessageId(chatMessage.getId());
                swiperMatch.setLastChatMessageId(chatMessage.getId());
                swiperMatch.setLastChatMessageBody(chatMessage.getBody());
                swipedMatch.setLastChatMessageId(chatMessage.getId());
                swipedMatch.setLastChatMessageBody(chatMessage.getBody());
            }
        }
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
        LocalDate birth = randomBirth();
        Date now = new Date();

        String name = "user-" + count;
        account.setName(name);
//        account.setPoint(50000);
//        account.setFreeSwipe(2000);
//        account.setFreeSwipeUpdatedAt(now);
        account.setProfilePhotoKey(UUID.randomUUID().toString() + ".jpg");
        account.setCreatedAt(now);
        account.setUpdatedAt(now);
        Wallet wallet = new Wallet();
        wallet.setAccount(account);
        wallet.setPoint(50000);
        wallet.setFreeSwipe(2000);
        wallet.setFreeSwipeRechargedAt(now);
//        account.setWallet(wallet);
//        walletDAO.persist(wallet);


        Point point = geometryFactory.createPoint(new Coordinate(lon, lat));
        point.setSRID(4326);

        // profile
        Profile profile = new Profile(account,
                                      name,
                                      birth.getYear(),
                                      birth,
                                      random.nextBoolean(),
                                      random.nextInt(50) + 150,
                                      count + ": this is my profile",
                                      point,
                                      true,
                                      now);

        profile.setEnabled(true);

        // photo

        for (int p = 0; p < 5; p++) {
            Photo photo = new Photo();
            photo.setPhotoId(new PhotoId(account.getId(), UUID.randomUUID().toString() + ".jpg"));
            photo.setSequence(p);
            photo.setAccount(account);
            photo.setCreatedAt(new Date());
            photo.setUpdatedAt(new Date());
            account.getPhotos().add(photo);
        }

        PushSetting pushSetting = new PushSetting();
        pushSetting.setMatchPush(true);
        pushSetting.setSwipePush(true);
        pushSetting.setChatMessagePush(true);
        pushSetting.setAccount(account);

        entityManager.persist(account);
        entityManager.persist(profile);
        entityManager.persist(wallet);
        entityManager.persist(pushSetting);

    }

    private LocalDate randomBirth() {
        Random random = new Random();
        int year = random.nextInt((2003 - 1970)) + 1970;
        int month = random.nextInt((12 - 1));
        int day = random.nextInt((25 - 1)) + 1;
        return LocalDate.of(year, month, day);
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
//            swipe.setSwipeId(new SwipeId(swiper.getId(), swiped.getId()));
            swipe.setSwiper(swiper);
            swipe.setSwiped(swiped);
            swipe.setClicked(random.nextBoolean());
            swipe.setCount((random.nextInt(10) + 1));
            swipe.setCreatedAt(now);
            swipe.setUpdatedAt(now);
            entityManager.persist(swipe);
//            swiper.getSwipes().add(swipe);
        }
        entityManager.persist(swiper);
    }

    @PostMapping("/update/account-and-match")
    public void updateAccountAndMatch() throws InterruptedException {
        List<Account> accounts = new JPAQueryFactory(entityManager).selectFrom(QAccount.account).fetch();
        Random random = new Random();
//        for (Account account : accounts) {
//            if (random.nextBoolean()) {
//                account.setUpdatedAt(new Date());
//                Thread.sleep(10);
//            } else if (account.getMatches().size() > 0) {
//                Match match = account.getMatches().get(0);
//                List<Match> matches = new JPAQueryFactory(entityManager).selectFrom(QMatch.match)
//                                                                        .where(QMatch.match.chatId.eq(match.getChatId()))
//                                                                        .fetch();
//                Date date = new Date();
//                for (Match match1 : matches) {
//                    match1.setUpdatedAt(date);
//                }
//            }
//        }
    }

    @PostMapping("/update/rep-photo-updated-at")
    public void updateMatchUpdatedAt() {

    }


    @Transactional
    @PostMapping("/create/match")
    public void createDummyMatch() {


//        List<Swipe> swipes = entityManager.unwrap(Session.class).createQuery("select s1 from Swipe s1 " +
//                                                                             "inner join Swipe s2 on s1.swipeId.swipedId = s2.swipeId.swiperId " +
//                                                                             "where s1.clicked = true and s2.clicked = true " +
//                                                                             "and s1.swipeId.swiperId = s2.swipeId.swipedId order by s1.swipeId.swiperId",
//                                                                             Swipe.class).getResultList();
//        HashMap<String, Match> matchMap = new HashMap<>();
//        Random random = new Random();
//
//        Date date = new Date();
//
//        for (Swipe swipe : swipes) {
//
//            if (random.nextBoolean()) {
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTime(date);
//                calendar.add(Calendar.HOUR, -1);
//                date = calendar.getTime();
//            }
//
//            Chat chat = new Chat();
//
//            if (matchMap.containsKey(swipe.getSwipeId().getSwipedId().toString() + swipe.getSwipeId().getSwiperId().toString())) {
//                chat = matchMap.get(swipe.getSwipeId().getSwipedId().toString() + swipe.getSwipeId().getSwiperId().toString()).getChat();
//            }
//            swipe.setMatched(true);
//
//            Match newMatch = new Match();
//            newMatch.setChat(chat);
//            newMatch.setSwiper(swipe.getSwiper());
//            newMatch.setSwiped(swipe.getSwiped());
//            newMatch.setUnmatcher(false);
//
//            newMatch.setCreatedAt(date);
//            newMatch.setUpdatedAt(date);
//
//            MatchId matchId = new MatchId(swipe.getSwipeId().getSwiperId(), swipe.getSwipeId().getSwipedId());
//            newMatch.setMatchId(matchId);
//
//            chatDAO.persist(chat);
//            matchDAO.persist(newMatch);
//
//            matchMap.put(swipe.getSwipeId().getSwiperId().toString() + swipe.getSwipeId().getSwipedId().toString(), newMatch);
//        }
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
//                    innerCount++;
//                    boolean who = random.nextBoolean();
//                    Account sender = who ? match.getSwiper() : match.getSwiped();
//                    Account receiver = who ? match.getSwiped() : match.getSwiper();
//                    ChatMessage chatMessage = new ChatMessage(match.getChat(),
//                                                              receiver,
//                                                              "message-" + random.nextFloat(),
//                                                              date);
//                    match.getChat().getChatMessages().add(chatMessage);
//
//                    SentChatMessage sentChatMessage = new SentChatMessage(chatMessage, sender, innerCount, date);
//                    sentChatMessageDAO.persist(sentChatMessage);


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

    @GetMapping("/send/chat/message/stomp")
    public void sendDummyChatMessageToStomp(@RequestParam("accountId") UUID accountId,
                                            @RequestParam("recipientId") UUID recipientId,
                                            @RequestParam("chatId") UUID chatId,
                                            @RequestParam("body") String body) {
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setChatId(chatId);
        chatMessageDTO.setSenderId(accountId);
        chatMessageDTO.setBody(body);
        chatMessageDTO.setTag(UUID.randomUUID());
        SaveChatMessageDTO saveChatMessageDTO = chatService.saveChatMessage(chatMessageDTO);
        chatMessageDTO.setId(saveChatMessageDTO.getId());
        chatMessageDTO.setRecipientId(saveChatMessageDTO.getRecipientId());
        chatMessageDTO.setCreatedAt(saveChatMessageDTO.getCreatedAt());
        chatMessageDTO.setTag(null);
        stompService.push(chatMessageDTO, Locale.getDefault());
    }

    @GetMapping("/send/chat/message")
    public void sendDummyChatMessageToFCM(@RequestParam("body") String body) {
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setSenderId(UUID.fromString("5b677c5b-46c3-4749-83f6-704cd3c21ab8"));
        chatMessageDTO.setRecipientId(UUID.fromString("2c2743bf-23ab-4e23-bd4e-4955b8191e12"));
        chatMessageDTO.setBody(body);
//        chatMessageDTO.setChatId(1L);
//        chatMessageDTO.setId(UUID.fromString("ecbeacc4-3409-4195-8c47-76a1f6a4798c"));
        chatMessageDTO.setCreatedAt(new Date());
        stompService.pushChatMessage(chatMessageDTO, Locale.getDefault());
    }

    @GetMapping("/send/swipe")
    public void sendDummySwipe(@RequestParam("swipedId") UUID swipedId) {
        SwipeDTO swipeDTO = new SwipeDTO();
        Random random = new Random();
        swipeDTO.setId(random.nextLong());
        swipeDTO.setSwipedId(swipedId);
        swipeDTO.setSwiperId(UUID.fromString("4b6a2ce3-eb48-4d05-89e7-9b2f086bf84b"));
        swipeDTO.setUpdatedAt(new Date());
        swipeDTO.setClicked(false);
        stompService.push(swipeDTO, Locale.getDefault());
    }

    @GetMapping("/send/match")
    public void sendDummyMatch() {
        MatchDTO matchDTO = new MatchDTO();
        matchDTO.setSwiperId(UUID.fromString("2c2743bf-23ab-4e23-bd4e-4955b8191e12"));
        matchDTO.setSwipedId(UUID.randomUUID());
        matchDTO.setSwipedName("Test user");
        matchDTO.setChatId(UUID.randomUUID());
        matchDTO.setLastChatMessageId(0L);
        matchDTO.setCreatedAt(new Date());
        matchDTO.setLastChatMessageBody("");
        matchDTO.setId(10L);
        matchDTO.setSwipedDeleted(false);
        matchDTO.setUnmatched(false);
        stompService.push(matchDTO, Locale.getDefault());
//        MatchDTO matchDTO = new MatchDTO(1L, UUID.randomUUID(), false, "Michael", "profilephotoeky", false, true, new Date(), new Date());
//        matchDTO.setPushType(PushType.MATCHED);
//        fcmService.sendMatch(matchDTO,
//                             "c1x4LYePRdO3X8hJnNe8Z2:APA91bHpnXUSjS4xPSfs5KxeNXmUsv1pCnJ7ch2Lnq7MxcjWlQdy7Z4kCBG3Vk1tEB4hC4XevyqMTtWJ2RfnHlplvJAdjeFpMHhnZcimwRMgruSb5lDzDPOmYu-ux7OO5Osduz9q_QHr",
//                             Locale.getDefault());
    }

    @GetMapping("/send/click")
    public void sendDummyClick() {
//        MatchDTO matchDTO = new MatchDTO(PushType.CLICKED);
//        matchDTO.setSwiperId(UUID.randomUUID());
//        matchDTO.setSwipedId(UUID.randomUUID());
//        matchDTO.setName("Michael");
//        matchDTO.setProfilePhotoKey("profile key");
//        matchDTO.setUpdatedAt(new Date());
//        matchDTO.setDeleted(false);
//        fcmService.sendMatch(matchDTO,
//                             "dNx-ay3rS0qnIITWvS2ue7:APA91bHTsw7heoTkdZXiIQTvIovZEQBcLIw-hIdlv-Ti141OeUjEjW860r2WlfzVtKGqX0gYhymGJ2b_VDbNY2Ao5SbCzeTjquIPmSYb3QunGgdRuqqU8scU3QGxtXTxV9FeKFdLKsEE",
//                             Locale.getDefault());
    }

    @GetMapping("/save/chat/message")
    public void saveDummyChatMessage(@RequestParam("body") String body) {
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setSenderId(UUID.fromString("5b677c5b-46c3-4749-83f6-704cd3c21ab8"));
        chatMessageDTO.setRecipientId(UUID.fromString("2c2743bf-23ab-4e23-bd4e-4955b8191e12"));
        chatMessageDTO.setBody(body);
//        chatMessageDTO.setChatId(1L);
//        chatMessageDTO.setId(UUID.fromString("ecbeacc4-3409-4195-8c47-76a1f6a4798c"));
        chatService.saveChatMessage(chatMessageDTO);
    }

    @GetMapping("/access-token-for-account")
    public String getAccessTokenFor(@RequestParam("accountId") UUID accountId) {
        Account account = accountDAO.findById(accountId, false);
        return jwtTokenProvider.createAccessToken(account.getId().toString(), account.getRoleNames());
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


    @Transactional
    @PostMapping("/create/swipe-for-account")
    public void createDummySwipeForAccount(@RequestParam UUID accountId) {
        Account swiper = accountDAO.findById(accountId, false);
        List<Account> accounts = new JPAQueryFactory(entityManager).selectFrom(QAccount.account).fetch();
        Random random = new Random();

        for (Account swiped : accounts) {
            if (swiper.getId().equals(swiped.getId())) continue;

            Date now = new Date();
//            Swipe subSwipe = new Swipe();
//            subSwipe.setSwiper(swiper);
//            subSwipe.setSwiped(swiped);
//            subSwipe.setClicked(random.nextBoolean());
//            subSwipe.setCount((random.nextInt(10) + 1));
//            subSwipe.setCreatedAt(now);
//            subSwipe.setUpdatedAt(now);

            now = new Date();
            Swipe objSwipe = new Swipe();
            objSwipe.setSwiper(swiped);
            objSwipe.setSwiped(swiper);
            objSwipe.setClicked(random.nextBoolean());
            objSwipe.setCount((random.nextInt(10) + 1));
            objSwipe.setCreatedAt(now);
            objSwipe.setUpdatedAt(now);

//            entityManager.persist(subSwipe);
            entityManager.persist(objSwipe);
        }

    }

    @Transactional
    @GetMapping("/create/matches/for/account")
    public void createDummyMatchesForAccount(@RequestParam("accountId") UUID accountId,
                                             @RequestParam("count") int count) {
        Account swiper = accountDAO.findById(accountId, false);
        List<Account> accounts = jpqlQueryFactory.selectFrom(QAccount.account).fetch();

        Random random = new Random();
        int matchCount = 0;
        for (Account account : accounts) {

            if (swiper == account) {
                continue;
            }
            UUID chatId = UUID.randomUUID();
            Match subMatch = new Match(swiper, account, chatId, new Date());
            Match objMatch = new Match(account, swiper, chatId, new Date());


//            if (random.nextInt(10) > 8) {
//                subMatch.setUnmatched(true);
//                objMatch.setUnmatched(true);
//
//                if (random.nextBoolean()) {
//                    subMatch.setDeleted(true);
//                }
//            }
//
//            if (random.nextInt(10) > 3) {
//                subMatch.setLastChatMessageId(random.nextInt(50) + 1);
//                subMatch.setLastReceivedChatMessageId(random.nextInt(50) + 1);
//                if (random.nextInt(10) > 7) {
//                    subMatch.setLastReadReceivedChatMessageId(subMatch.getLastReceivedChatMessageId());
//                } else {
//                    subMatch.setLastReadReceivedChatMessageId(random.nextInt((int) subMatch.getLastReceivedChatMessageId()));
//                }
//            }

            matchDAO.persist(subMatch);
            matchDAO.persist(objMatch);

            matchCount++;
            if (matchCount > count) {
                break;
            }
        }
    }


}
