//package com.beeswork.balanceaccountservice.restcontroller;
//
//import com.beeswork.balanceaccountservice.constant.ExceptionCode;
//import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
//import com.beeswork.balanceaccountservice.dao.question.QuestionDAO;
//import com.beeswork.balanceaccountservice.entity.account.Account;
//import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
//import com.beeswork.balanceaccountservice.entity.account.QAccount;
//import com.beeswork.balanceaccountservice.exception.BadRequestException;
//import com.beeswork.balanceaccountservice.exception.account.AccountBlockedException;
//import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
//import com.beeswork.balanceaccountservice.vm.account.SaveAnswersVM;
//import com.beeswork.balanceaccountservice.vm.account.SaveFCMTokenVM;
//import com.beeswork.balanceaccountservice.vm.account.SaveLocationVM;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.locationtech.jts.geom.Coordinate;
//import org.locationtech.jts.geom.GeometryFactory;
//import org.locationtech.jts.geom.Point;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.MessageSource;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.RequestBuilder;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import java.text.SimpleDateFormat;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//class AccountControllerIntTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private JPAQueryFactory jpaQueryFactory;
//
//    @Autowired
//    private MessageSource messageSource;
//
//    @Autowired
//    private AccountDAO accountDAO;
//
//    @Autowired
//    private QuestionDAO questionDAO;
//
//    @Autowired
//    private GeometryFactory geometryFactory;
//
//    private final QAccount qAccount = QAccount.account;
//
////  #################################################################################################  //
////  #################################################################################################  //
////  ################################      saveAnswers()      #######################################  //
////  #################################################################################################  //
////  #################################################################################################  //
//
//
////    @Test
////    @DisplayName("saveAnswers_shouldUpdateAnswers")
////    void saveAnswers_shouldUpdateAnswers() throws Exception {
////
////        Account account = jpaQueryFactory.selectFrom(qAccount)
////                                         .where(qAccount.enabled.eq(true).and(qAccount.blocked.eq(false)))
////                                         .fetchFirst();
////
////        SaveAnswersVM saveAnswersVM = getSaveAnswersArguments(account.getId().toString(), account.getEmail());
////
////        String params = objectMapper.writeValueAsString(saveAnswersVM);
////        System.out.println("saveAnswersWithInvalidEmail_shouldThrowAccountNotFoundException - params");
////        System.out.println(params);
////
////        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_ANSWERS)
////                                                              .contentType(MediaType.APPLICATION_JSON)
////                                                              .content(params);
////
////        mockMvc.perform(requestBuilder).andExpect(status().isOk());
////
////        account = accountDAO.findWithAccountQuestions(account.getId(), account.getEmail());
////
////        for (AccountQuestion accountQuestion : account.getAccountQuestions()) {
////            assertEquals(accountQuestion.isSelected(), saveAnswersVM.getAnswers().get(accountQuestion.getQuestionId()));
////        }
////    }
////
////    @Test
////    @DisplayName("saveAnswersWithBlockedAccount_shouldThrowAccountBlockedException")
////    void saveAnswersWithBlockedAccount_shouldThrowAccountBlockedException() throws Exception {
////
////        Account account = jpaQueryFactory.selectFrom(qAccount).where(qAccount.blocked.eq(true)).fetchFirst();
////
////        SaveAnswersVM saveAnswersVM = getSaveAnswersArguments(account.getId().toString(), account.getEmail());
////
////        String params = objectMapper.writeValueAsString(saveAnswersVM);
////        System.out.println("saveAnswersWithBlockedAccount_shouldThrowAccountBlockedException - params");
////        System.out.println(params);
////
////        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_ANSWERS)
////                                                              .contentType(MediaType.APPLICATION_JSON)
////                                                              .content(params);
////
////        mockMvc.perform(requestBuilder)
////               .andExpect(status().isBadRequest())
////               .andExpect(response -> assertTrue(response.getResolvedException() instanceof AccountBlockedException));
////    }
////
////    @Test
////    @DisplayName("saveAnswersWithRandomEmail_shouldThrowAccountNotFoundException")
////    void saveAnswersWithRandomEmail_shouldThrowAccountNotFoundException() throws Exception {
////
////        Account account = jpaQueryFactory.selectFrom(qAccount)
////                                         .where(qAccount.enabled.eq(true).and(qAccount.blocked.eq(false)))
////                                         .fetchFirst();
////
////        SaveAnswersVM saveAnswersVM = getSaveAnswersArguments(account.getId().toString(), account.getEmail());
////
////        saveAnswersVM.setEmail("1" + saveAnswersVM.getEmail());
////
////        String params = objectMapper.writeValueAsString(saveAnswersVM);
////        System.out.println("saveAnswersWithRandomEmail_shouldThrowAccountNotFoundException - params");
////        System.out.println(params);
////
////        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_ANSWERS)
////                                                              .contentType(MediaType.APPLICATION_JSON)
////                                                              .content(params);
////
////        mockMvc.perform(requestBuilder)
////               .andExpect(status().isNotFound())
////               .andExpect(response -> assertTrue(response.getResolvedException() instanceof AccountNotFoundException));
////    }
////
////
////    @Test
////    @DisplayName("saveAnswersWithRandomUUID_shouldThrowAccountNotFoundException")
////    void saveAnswersWithRandomUUID_shouldThrowAccountNotFoundException() throws Exception {
////
////        Account account = jpaQueryFactory.selectFrom(qAccount)
////                                         .where(qAccount.enabled.eq(true).and(qAccount.blocked.eq(false)))
////                                         .fetchFirst();
////
////        SaveAnswersVM saveAnswersVM = getSaveAnswersArguments(account.getId().toString(), account.getEmail());
////
////        saveAnswersVM.setAccountId(UUID.randomUUID().toString());
////
////        String params = objectMapper.writeValueAsString(saveAnswersVM);
////        System.out.println("saveAnswersWithRandomUUID_shouldThrowAccountNotFoundException - params");
////        System.out.println(params);
////
////        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_ANSWERS)
////                                                              .contentType(MediaType.APPLICATION_JSON)
////                                                              .content(params);
////
////        mockMvc.perform(requestBuilder)
////               .andExpect(status().isNotFound())
////               .andExpect(response -> assertTrue(response.getResolvedException() instanceof AccountNotFoundException));
////    }
////
////    @Test
////    @DisplayName("saveAnswersWithInvalidUUID_shouldThrowBadRequestException")
////    void saveAnswersWithInvalidUUID_shouldThrowBadRequestException() throws Exception {
////
////        Account account = jpaQueryFactory.selectFrom(qAccount)
////                                         .where(qAccount.enabled.eq(true).and(qAccount.blocked.eq(false)))
////                                         .fetchFirst();
////
////        SaveAnswersVM saveAnswersVM = getSaveAnswersArguments(account.getId().toString(), account.getEmail());
////
////        saveAnswersVM.setAccountId(RandomStringGenerator.generate(10));
////
////        String params = objectMapper.writeValueAsString(saveAnswersVM);
////        System.out.println("saveAnswersWithInvalidUUID_shouldThrowBadRequestException - params");
////        System.out.println(params);
////
////        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_ANSWERS)
////                                                              .contentType(MediaType.APPLICATION_JSON)
////                                                              .content(params);
////
////        mockMvc.perform(requestBuilder)
////               .andExpect(status().isBadRequest())
////               .andExpect(response -> assertTrue(response.getResolvedException() instanceof BadRequestException));
////    }
////
////    @Test
////    @DisplayName("saveAnswersWithInvalidEmail_shouldThrowBadRequestException")
////    void saveAnswersWithInvalidEmail_shouldThrowBadRequestException() throws Exception {
////
////        Account account = jpaQueryFactory.selectFrom(qAccount)
////                                         .where(qAccount.enabled.eq(true).and(qAccount.blocked.eq(false)))
////                                         .fetchFirst();
////
////        SaveAnswersVM saveAnswersVM = getSaveAnswersArguments(account.getId().toString(), account.getEmail());
////        saveAnswersVM.setEmail(RandomStringGenerator.generate(10));
////
////        String params = objectMapper.writeValueAsString(saveAnswersVM);
////        System.out.println("saveAnswersWithInvalidEmail_shouldThrowBadRequestException - params");
////        System.out.println(params);
////
////        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_ANSWERS)
////                                                              .contentType(MediaType.APPLICATION_JSON)
////                                                              .content(params);
////
////        mockMvc.perform(requestBuilder)
////               .andExpect(status().isBadRequest())
////               .andExpect(response -> assertTrue(response.getResolvedException() instanceof BadRequestException));
////    }
//
//    @Test
//    @DisplayName("saveAnswersWithInvalidArguments_shouldThrowBadRequestException")
//    void saveAnswersWithInvalidArguments_shouldThrowBadRequestException() throws Exception {
//
//        Account account = jpaQueryFactory.selectFrom(qAccount).where(qAccount.enabled.eq(true).and(qAccount.blocked.eq(false))).fetchFirst();
//
//        Map<String, Object> params = getSaveAnswersArguments(account.getId(),
//                                                              account.getEmail());
//
//        // saveAnswers() with empty accountId
//        performFieldErrorTest(URLPath.ACCOUNT_FCM_TOKEN,
//                              objectMapper.writeValueAsString(params),
//                              "saveAnswersWithEmptyAccountId", true);
//
//        // saveAnswers() with invalid accountId
//        params.put(Field.ACCOUNT_ID, RandomStringGenerator.generate(10));
//        performFieldErrorTest(URLPath.ACCOUNT_FCM_TOKEN,
//                              objectMapper.writeValueAsString(params),
//                              "saveAnswersWithInvalidAccountId", true);
//
//        // saveAnswers() with invalid email
//        params.put(Field.ACCOUNT_ID, UUID.randomUUID().toString());
//        params.put(Field.EMAIL, RandomStringGenerator.generate(10));
//        performFieldErrorTest(URLPath.ACCOUNT_FCM_TOKEN,
//                              objectMapper.writeValueAsString(params),
//                              "saveAnswersWithInvalidEmail", true);
//
//        // saveAnswers() with empty email
//        params.put(Field.EMAIL, "");
//        performFieldErrorTest(URLPath.ACCOUNT_FCM_TOKEN,
//                              objectMapper.writeValueAsString(params),
//                              "saveAnswersWithEmptyEmail", true);
//
//    }
//
////  TODO: add oversize answers throw exception
//    private Map<String, Object> getSaveAnswersArguments(Object accountId, Object email) {
//
//        Map<String, Object> map = new HashMap<>();
//        map.put(Field.ACCOUNT_ID, accountId);
//        map.put(Field.EMAIL, email);
//
//        Map<Long, Boolean> answersMap = new HashMap<>();
//
//        Account account = accountDAO.findWithAccountQuestions(UUID.fromString(accountId.toString()), email.toString());
//
//        long questionCount = questionDAO.count();
//
//        Random random = new Random();
//
//        for (AccountQuestion accountQuestion : account.getAccountQuestions()) {
//            long questionId = accountQuestion.getQuestionId();
//            if (questionId == questionCount) questionId = 1;
//            else questionId++;
//            answersMap.put(questionId, random.nextBoolean());
//        }
//
//        map.put(Field.ANSWERS, answersMap);
//        return map;
//    }
//
//
////  #################################################################################################  //
////  #################################################################################################  //
////  ################################      savaFCMToken()      #######################################  //
////  #################################################################################################  //
////  #################################################################################################  //
//
//    @Test
//    @DisplayName("saveFCMToken_shouldUpdateFCMTokenField")
//    void saveFCMToken_shouldUpdateFCMTokenField() throws Exception {
//
//        Account account = jpaQueryFactory.selectFrom(qAccount)
//                                         .where(qAccount.enabled.eq(true).and(qAccount.blocked.eq(false)))
//                                         .fetchFirst();
//
//        Map<String, Object> params = getSaveFCMTokenArguments(account.getId(),
//                                                              account.getEmail(),
//                                                              RandomStringGenerator.generate(10));
//
//        mockMvc.perform(requestBuilder(URLPath.ACCOUNT_FCM_TOKEN, objectMapper.writeValueAsString(params), true))
//               .andExpect(status().isOk());
//
//        account = jpaQueryFactory.selectFrom(qAccount).where(qAccount.id.eq(account.getId())).fetchOne();
//        assertEquals(params.get(Field.TOKEN), account.getFcmToken());
//    }
//
//    @Test
//    @DisplayName("saveFCMTokenWithBlockedAccount_shouldThrowAccountBlockedException")
//    void saveFCMTokenWithBlockedAccount_shouldThrowAccountBlockedException() throws Exception {
//
//        Account account = jpaQueryFactory.selectFrom(qAccount).where(qAccount.blocked.eq(true)).fetchFirst();
//
//        Map<String, Object> params = getSaveFCMTokenArguments(account.getId(),
//                                                              account.getEmail(),
//                                                              RandomStringGenerator.generate(10));
//
//        performIdentityErrorTest(URLPath.ACCOUNT_FCM_TOKEN,
//                                 objectMapper.writeValueAsString(params),
//                                 "saveFCMTokenWithBlockedAccount", true, true);
//    }
//
//    @Test
//    @DisplayName("saveFCMTokenWithInvalidAccount_shouldThrowAccountNotFoundException")
//    void saveFCMTokenWithInvalidAccount_shouldThrowAccountNotFoundException() throws Exception {
//
//        Account account = jpaQueryFactory.selectFrom(qAccount).where(qAccount.blocked.eq(false)).fetchFirst();
//
//        Map<String, Object> params = getSaveFCMTokenArguments(account.getId(),
//                                                              account.getEmail(),
//                                                              RandomStringGenerator.generate(10));
//
//        String validArguments = objectMapper.writeValueAsString(params);
//
//        // saveFCMTokenWithRandomUUId
//        params.put(Field.ACCOUNT_ID, UUID.randomUUID().toString());
//        performIdentityErrorTest(URLPath.ACCOUNT_FCM_TOKEN,
//                                 objectMapper.writeValueAsString(params),
//                                 "saveFCMTokenWithRandomUUId | valid arguments " + validArguments, false, true);
//
//        // saveFCMTokenWithRandomEmail
//        params.put(Field.ACCOUNT_ID, account.getId());
//        params.put(Field.EMAIL, "2" + account.getEmail());
//        performIdentityErrorTest(URLPath.ACCOUNT_FCM_TOKEN,
//                                 objectMapper.writeValueAsString(params),
//                                 "saveFCMTokenWithRandomEmail | valid arguments " + validArguments, false, true);
//
//    }
//
//    @Test
//    @DisplayName("saveFCMTokenWithInvalidArguments_shouldThrowBadRequestException")
//    void saveFCMTokenWithInvalidArguments_shouldThrowBadRequestException() throws Exception {
//
//        Map<String, Object> params = getSaveFCMTokenArguments("",
//                                                              "c0go2@naver.com",
//                                                              RandomStringGenerator.generate(10));
//
//        // saveFCMToken() with empty accountId
//        performFieldErrorTest(URLPath.ACCOUNT_FCM_TOKEN,
//                              objectMapper.writeValueAsString(params),
//                              "saveFCMTokenWithEmptyAccountId", true);
//
//        // saveFCMToken() with invalid accountId
//        params.put(Field.ACCOUNT_ID, RandomStringGenerator.generate(10));
//        performFieldErrorTest(URLPath.ACCOUNT_FCM_TOKEN,
//                              objectMapper.writeValueAsString(params),
//                              "saveFCMTokenWithInvalidAccountId", true);
//
//        // saveFCMToken() with invalid email
//        params.put(Field.ACCOUNT_ID, UUID.randomUUID().toString());
//        params.put(Field.EMAIL, RandomStringGenerator.generate(10));
//        performFieldErrorTest(URLPath.ACCOUNT_FCM_TOKEN,
//                              objectMapper.writeValueAsString(params),
//                              "saveFCMTokenWithInvalidEmail", true);
//
//        // saveFCMToken() with empty email
//        params.put(Field.EMAIL, "");
//        performFieldErrorTest(URLPath.ACCOUNT_FCM_TOKEN,
//                              objectMapper.writeValueAsString(params),
//                              "saveFCMTokenWithEmptyEmail", true);
//
//        // saveFCMToken() with empty token
//        params.put(Field.EMAIL, "c0go2@naver.com");
//        params.put(Field.TOKEN, "");
//        performFieldErrorTest(URLPath.ACCOUNT_FCM_TOKEN,
//                              objectMapper.writeValueAsString(params),
//                              "saveFCMTokenWithEmptyToken", true);
//
//    }
//
//    private Map<String, Object> getSaveFCMTokenArguments(Object accountId, Object email, Object token) {
//        Map<String, Object> params = new HashMap<>();
//        params.put(Field.ACCOUNT_ID, accountId);
//        params.put(Field.EMAIL, email);
//        params.put(Field.TOKEN, token);
//        return params;
//    }
//
//
////  #################################################################################################  //
////  #################################################################################################  //
////  ################################      savaLocation()      #######################################  //
////  #################################################################################################  //
////  #################################################################################################  //
//
//
//    @Test
//    @DisplayName("saveLocation_shouldUpdateLocationField")
//    void saveLocation_shouldUpdateLocationField() throws Exception {
//
//        Account account = jpaQueryFactory.selectFrom(qAccount).fetchFirst();
//        Map<String, Object> paramsMap = new HashMap<>();
//        paramsMap.put(Field.ACCOUNT_ID, account.getId());
//        paramsMap.put(Field.EMAIL, account.getEmail());
//
//        Random random = new Random();
//        double latitude = 32.0 + random.nextDouble();
//        double longitude = 122.0 + random.nextDouble();
//
//        paramsMap.put(Field.LATITUDE, latitude);
//        paramsMap.put(Field.LONGITUDE, longitude);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_LOCATION)
//                                                              .contentType(MediaType.APPLICATION_JSON)
//                                                              .content(objectMapper.writeValueAsString(paramsMap));
//
//        mockMvc.perform(requestBuilder).andExpect(status().isOk());
//
//        account = jpaQueryFactory.selectFrom(qAccount).where(qAccount.id.eq(account.getId())).fetchOne();
//        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
//        assertEquals(account.getLocation(), point);
//    }
//
//
//    @Test
//    @DisplayName("saveLocationWithBlockedAccount_shouldThrowAccountBlockedException")
//    void saveLocationWithBlockedAccount_shouldThrowAccountBlockedException() throws Exception {
//
//        Account account = jpaQueryFactory.selectFrom(qAccount).where(qAccount.blocked.eq(true)).fetchFirst();
//
//        Map<String, Object> params = getSaveLocationArguments(account.getId(),
//                                                              account.getEmail(),
//                                                              32.2,
//                                                              128.23);
//
//        performIdentityErrorTest(URLPath.ACCOUNT_LOCATION,
//                                 objectMapper.writeValueAsString(params),
//                                 "saveLocationWithBlockedAccount", true, true);
//    }
//
//    @Test
//    @DisplayName("saveLocationWithInvalidAccount_shouldThrowAccountNotFoundException")
//    void saveLocationWithInvalidAccount_shouldThrowAccountNotFoundException() throws Exception {
//
//        Account account = jpaQueryFactory.selectFrom(qAccount).where(qAccount.blocked.eq(false)).fetchFirst();
//
//        Map<String, Object> params = getSaveLocationArguments(account.getId(),
//                                                              account.getEmail(),
//                                                              32.34,
//                                                              128.12);
//
//        String validArguments = objectMapper.writeValueAsString(params);
//
//        // saveLocationWithRandomUUId
//        params.put(Field.ACCOUNT_ID, UUID.randomUUID().toString());
//        performIdentityErrorTest(URLPath.ACCOUNT_LOCATION,
//                                 objectMapper.writeValueAsString(params),
//                                 "saveLocationWithRandomUUId | valid arguments " + validArguments, false, true);
//
//        // saveLocationWithRandomEmail
//        params.put(Field.ACCOUNT_ID, account.getId());
//        params.put(Field.EMAIL, "2" + account.getEmail());
//        performIdentityErrorTest(URLPath.ACCOUNT_LOCATION,
//                                 objectMapper.writeValueAsString(params),
//                                 "saveLocationWithRandomEmail | valid arguments " + validArguments, false, true);
//
//    }
//
//
//    @Test
//    @DisplayName("saveLocationWithInvalidArguments_shouldThrowBadRequestException")
//    void saveLocationWithInvalidArguments_shouldThrowBadRequestException() throws Exception {
//
//        Map<String, Object> params = getSaveLocationArguments("",
//                                                              "c0go2@naver.com",
//                                                              32.45,
//                                                              128.12);
//
//        // saveLocation() with empty accountId
//        performFieldErrorTest(URLPath.ACCOUNT_LOCATION,
//                              objectMapper.writeValueAsString(params),
//                              "saveLocationWithEmptyAccountId", true);
//
//        // saveLocation() with invalid accountId
//        params.put(Field.ACCOUNT_ID, RandomStringGenerator.generate(10));
//
//        performFieldErrorTest(URLPath.ACCOUNT_LOCATION,
//                              objectMapper.writeValueAsString(params),
//                              "saveLocationWithInvalidAccountId", true);
//
//        // saveLocation() with invalid email
//        params.put(Field.ACCOUNT_ID, UUID.randomUUID().toString());
//        params.put(Field.EMAIL, RandomStringGenerator.generate(10));
//
//        performFieldErrorTest(URLPath.ACCOUNT_LOCATION,
//                              objectMapper.writeValueAsString(params),
//                              "saveLocationWithInvalidEmail", true);
//
//        // saveLocation() with empty email
//        params.put(Field.EMAIL, "");
//
//        performFieldErrorTest(URLPath.ACCOUNT_LOCATION,
//                              objectMapper.writeValueAsString(params),
//                              "saveLocationWithEmptyEmail", true);
//
//        // saveLocation() with empty latitude
//        params.put(Field.EMAIL, "c0go2@naver.com");
//        params.put(Field.LATITUDE, "");
//
//        performFieldErrorTest(URLPath.ACCOUNT_LOCATION,
//                              objectMapper.writeValueAsString(params),
//                              "saveLocationWithEmptyLatitude", true);
//
//        // saveLocation() with less latitude
//        params.put(Field.LATITUDE, "-162.32");
//
//        performFieldErrorTest(URLPath.ACCOUNT_LOCATION,
//                              objectMapper.writeValueAsString(params),
//                              "saveLocationWithLessLatitude", true);
//
//        // saveLocation() with larger latitude
//        params.put(Field.LATITUDE, "162.32");
//
//        performFieldErrorTest(URLPath.ACCOUNT_LOCATION,
//                              objectMapper.writeValueAsString(params),
//                              "saveLocationWithLargerLatitude", true);
//
//        // saveLocation() with empty longitude
//        params.put(Field.LATITUDE, "32.24");
//        params.put(Field.LONGITUDE, "");
//
//        performFieldErrorTest(URLPath.ACCOUNT_LOCATION,
//                              objectMapper.writeValueAsString(params),
//                              "saveLocationWithEmptyLongitude", true);
//
//        // saveLocation() with less longitude
//        params.put(Field.LONGITUDE, "-360.24");
//
//        performFieldErrorTest(URLPath.ACCOUNT_LOCATION,
//                              objectMapper.writeValueAsString(params),
//                              "saveLocationWithLessLongitude", true);
//
//        // saveLocation() with larger longitude
//        params.put(Field.LONGITUDE, "360.24");
//
//        performFieldErrorTest(URLPath.ACCOUNT_LOCATION,
//                              objectMapper.writeValueAsString(params),
//                              "saveLocationWithLargerLongitude", true);
//
//    }
//
//
//    private Map<String, Object> getSaveLocationArguments(Object accountId, Object email, Object latitude, Object longitude) {
//
//        Map<String, Object> map = new HashMap<>();
//        map.put(Field.ACCOUNT_ID, accountId);
//        map.put(Field.EMAIL, email);
//        map.put(Field.LATITUDE, latitude);
//        map.put(Field.LONGITUDE, longitude);
//        return map;
//    }
//
//
////  #################################################################################################  //
////  #################################################################################################  //
////  ################################      SaveProfile()      ########################################  //
////  #################################################################################################  //
////  #################################################################################################  //
//
//    //@Test
//    @DisplayName("saveProfileWithAccountEnabled_shouldUpdatePartialFields")
//    void saveProfileWithAccountEnabled_shouldUpdatePartialFields() throws Exception {
//
//        Map paramsMap = getValidSaveProfileArgumentsAsMap(false, true);
//
//        System.out.println("saveProfileWithAccountEnabled - params");
//        paramsMap.forEach((key, value) -> System.out.printf("%s=%s%n", key, value));
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_PROFILE)
//                                                              .contentType(MediaType.APPLICATION_JSON)
//                                                              .content(objectMapper.writeValueAsString(paramsMap));
//
//        mockMvc.perform(requestBuilder).andExpect(status().isOk());
//
//        Account account = jpaQueryFactory.selectFrom(qAccount).where(qAccount.id.eq(UUID.fromString(paramsMap.get(Field.ACCOUNT_ID).toString()))).fetchOne();
//
//        assertNotEquals(paramsMap.get(Field.NAME).toString(), account.getName());
//        assertNotEquals(Boolean.valueOf(paramsMap.get(Field.GENDER).toString()), account.isGender());
//
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Date birthParam = simpleDateFormat.parse(paramsMap.get(Field.BIRTH).toString());
//
//        assertNotEquals(DateTimeFormatter.ISO_INSTANT.format(account.getBirth().toInstant()),
//                        DateTimeFormatter.ISO_INSTANT.format(birthParam.toInstant()));
//
//        assertEquals(account.getAbout(), paramsMap.get(Field.ABOUT));
//
//        if (account.getHeight() != null)
//            assertNotEquals(account.getHeight().toString(), paramsMap.get(Field.HEIGHT).toString());
//
//    }
//
//    //@Test
//    @DisplayName("saveProfileWithAccountDisabled_shouldUpdateAllFields")
//    void saveProfileWithAccountDisabled_shouldUpdateAllFields() throws Exception {
//
//        Map paramsMap = getValidSaveProfileArgumentsAsMap(false, false);
//
//        System.out.println("saveProfileWithAccountDisabled - params");
//        paramsMap.forEach((key, value) -> System.out.printf("%s=%s%n", key, value));
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_PROFILE)
//                                                              .contentType(MediaType.APPLICATION_JSON)
//                                                              .content(objectMapper.writeValueAsString(paramsMap));
//
//        mockMvc.perform(requestBuilder).andExpect(status().isOk());
//
//        Account account = jpaQueryFactory.selectFrom(qAccount).where(qAccount.id.eq(UUID.fromString(paramsMap.get(Field.ACCOUNT_ID).toString()))).fetchOne();
//
//        assertEquals(paramsMap.get(Field.NAME).toString(), account.getName());
//        assertEquals(paramsMap.get(Field.ABOUT).toString(), account.getAbout());
//        assertTrue(account.isEnabled());
//        assertEquals(Boolean.valueOf(paramsMap.get(Field.GENDER).toString()), account.isGender());
//
//
//        assertEquals(account.getHeight().toString(), paramsMap.get(Field.HEIGHT).toString());
//
//    }
//
//    //    //@Test
//    @DisplayName("saveProfileWithInvalidEmail_shouldThrowAccountNotFoundException")
//    void saveProfileWithInvalidEmail_shouldThrowAccountNotFoundException() throws Exception {
//
//        Map paramsMap = getValidSaveProfileArgumentsAsMap(false, true);
//        paramsMap.put(Field.EMAIL, RandomStringGenerator.generate(10) + "@gmail.com");
//
//        System.out.println("saveProfileWithInvalidEmail_shouldThrowAccountNotFoundException - params");
//        paramsMap.forEach((key, value) -> System.out.printf("%s=%s%n", key, value));
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_PROFILE)
//                                                              .contentType(MediaType.APPLICATION_JSON)
//                                                              .content(objectMapper.writeValueAsString(paramsMap));
//
//        mockMvc.perform(requestBuilder)
//               .andExpect(status().isNotFound())
//               .andExpect(response -> assertTrue(response.getResolvedException() instanceof AccountNotFoundException));
//    }
//
//    //    //@Test
//    @DisplayName("saveProfileWithBlockedAccount_shouldThrowAccountBlockedException")
//    void saveProfileWithBlockedAccount_shouldThrowAccountBlockedException() throws Exception {
//
//        Map paramsMap = getValidSaveProfileArgumentsAsMap(true, true);
//        System.out.println("saveProfileWithBlockedAccount_shouldThrowAccountBlockedException - params");
//        paramsMap.forEach((key, value) -> System.out.printf("%s=%s%n", key, value));
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_PROFILE)
//                                                              .contentType(MediaType.APPLICATION_JSON)
//                                                              .content(objectMapper.writeValueAsString(paramsMap));
//
//        mockMvc.perform(requestBuilder)
//               .andExpect(status().isBadRequest())
//               .andExpect(response -> assertTrue(response.getResolvedException() instanceof AccountBlockedException));
//
//    }
//
//    //    //@Test
//    @DisplayName("saveProfileWithInvalidUUID_shouldThrowAccountNotFoundException")
//    void saveProfileWithInvalidUUID_shouldThrowAccountNotFoundException() throws Exception {
//
//        Map paramsMap = getValidSaveProfileArgumentsAsMap(false, true);
//        paramsMap.put(Field.ACCOUNT_ID, UUID.randomUUID());
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_PROFILE)
//                                                              .contentType(MediaType.APPLICATION_JSON)
//                                                              .content(objectMapper.writeValueAsString(paramsMap));
//
//        mockMvc.perform(requestBuilder)
//               .andExpect(status().isNotFound())
//               .andExpect(response -> assertTrue(response.getResolvedException() instanceof AccountNotFoundException));
//    }
//
//    //    //@Test
//    @DisplayName("saveProfileWithEmptyArguments")
//    void saveProfileWithEmptyArguments_shouldThrowFieldException() throws Exception {
//
//        Map paramsMap = getSaveProfileArgumentsAsMap("", null, "", "", null, "999", "");
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_PROFILE)
//                                                              .contentType(MediaType.APPLICATION_JSON)
//                                                              .content(objectMapper.writeValueAsString(paramsMap));
//
//        MockHttpServletResponse response = mockMvc.perform(requestBuilder)
//                                                  .andReturn()
//                                                  .getResponse();
//
//        Map content = objectMapper.readValue(response.getContentAsString(), Map.class);
//        Map<String, String> fieldErrorMessages = (Map<String, String>) content.get(Field.FIELD_ERROR_MESSAGES);
//
//        // check if error is fieldException
//        assertEquals(ExceptionCode.FIELD_EXCEPTION, content.get(Field.ERROR));
//
//        // check if the number of errors is 6
//        assertEquals(6, fieldErrorMessages.size());
//
//        // check if error messages are correct
//        assertEquals(getErrorMessage(FieldError.UUID_EMPTY), fieldErrorMessages.get(Field.ACCOUNT_ID));
//        assertEquals(getErrorMessage(FieldError.NAME_EMPTY), fieldErrorMessages.get(Field.NAME));
//        assertEquals(getErrorMessage(FieldError.EMAIL_EMPTY), fieldErrorMessages.get(Field.EMAIL));
//        assertEquals(getErrorMessage(FieldError.BIRTH_NULL), fieldErrorMessages.get(Field.BIRTH));
//        assertEquals(getErrorMessage(FieldError.ABOUT_EMPTY), fieldErrorMessages.get(Field.ABOUT));
//        assertEquals(getErrorMessage(FieldError.GENDER_NULL), fieldErrorMessages.get(Field.GENDER));
//    }
//
//    //    //@Test
//    @DisplayName("saveProfileWithInvalidArguments")
//    void saveProfileWithInvalidArguments_shouldThrowFieldException() throws Exception {
//
//        Map paramsMap = getSaveProfileArgumentsAsMap("12322-32432-1232",
//                                                     RandomStringGenerator.generate(Field.NAME_MAX + 1),
//                                                     "c2Gmail.com",
//                                                     "2030-11-11",
//                                                     RandomStringGenerator.generate(Field.ABOUT_MAX + 1),
//                                                     "999",
//                                                     "false");
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_PROFILE)
//                                                              .contentType(MediaType.APPLICATION_JSON)
//                                                              .content(objectMapper.writeValueAsString(paramsMap));
//
//        MockHttpServletResponse response = mockMvc.perform(requestBuilder)
//                                                  .andReturn()
//                                                  .getResponse();
//
//        Map content = objectMapper.readValue(response.getContentAsString(), Map.class);
//        Map<String, String> fieldErrorMessages = (Map<String, String>) content.get(Field.FIELD_ERROR_MESSAGES);
//
//        // check if error is fieldException
//        assertEquals(ExceptionCode.FIELD_EXCEPTION, content.get(Field.ERROR));
//
//        // check if the number of errors is 4
//        assertEquals(4, fieldErrorMessages.size());
//
//        // check if error messages are correct
//        assertEquals(getErrorMessage(FieldError.UUID_INVALID), fieldErrorMessages.get(Field.ACCOUNT_ID));
//        assertEquals(getErrorMessage(FieldError.EMAIL_INVALID), fieldErrorMessages.get(Field.EMAIL));
//
//        String nameLengthErrorMessage = messageSource.getMessage(FieldError.NAME_LENGTH, null, Locale.KOREA);
//        nameLengthErrorMessage = nameLengthErrorMessage.replaceAll("\\{min\\}", String.valueOf(Field.NAME_MIN));
//        nameLengthErrorMessage = nameLengthErrorMessage.replaceAll("\\{max\\}", String.valueOf(Field.NAME_MAX));
//        assertEquals(nameLengthErrorMessage, fieldErrorMessages.get(Field.NAME));
//
//        String aboutLengthErrorMessage = messageSource.getMessage(FieldError.ABOUT_LENGTH, null, Locale.KOREA);
//        aboutLengthErrorMessage = aboutLengthErrorMessage.replaceAll("\\{min\\}", String.valueOf(Field.ABOUT_MIN));
//        aboutLengthErrorMessage = aboutLengthErrorMessage.replaceAll("\\{max\\}", String.valueOf(Field.ABOUT_MAX));
//        assertEquals(aboutLengthErrorMessage, fieldErrorMessages.get(Field.ABOUT));
//
//    }
//
//    private Map<String, String> getValidSaveProfileArgumentsAsMap(boolean blocked, boolean enabled) {
//
//        Account account = jpaQueryFactory.selectFrom(qAccount)
//                                         .where(qAccount.blocked.eq(blocked).and(qAccount.enabled.eq(enabled)))
//                                         .fetchFirst();
//
//        return getSaveProfileArgumentsAsMap(account.getId().toString(),
//                                            RandomStringGenerator.generate(10),
//                                            account.getEmail(),
//                                            "1910-01-01",
//                                            RandomStringGenerator.generate(100),
//                                            String.valueOf(999),
//                                            String.valueOf((!account.isGender())));
//    }
//
//    private Map<String, String> getSaveProfileArgumentsAsMap(String accountId, String name, String email, String birth, String about, String height, String gender) {
//
//        Map<String, String> arguments = new HashMap<>();
//        arguments.put(Field.ACCOUNT_ID, accountId);
//        arguments.put(Field.NAME, name);
//        arguments.put(Field.EMAIL, email);
//        arguments.put(Field.BIRTH, birth);
//        arguments.put(Field.ABOUT, about);
//        arguments.put(Field.HEIGHT, height);
//        arguments.put(Field.GENDER, gender);
//        return arguments;
//    }
//
//    private String getErrorMessage(String code) {
//        return getErrorMessage(code, null);
//    }
//
//    private String getErrorMessage(String code, Object[] args) {
//        return messageSource.getMessage(code, args, Locale.KOREA);
//    }
//
//
//    private MockHttpServletRequestBuilder requestBuilder(String url, String params, boolean isPost) {
//
//        MockHttpServletRequestBuilder requestBuilder;
//        if (isPost) requestBuilder = MockMvcRequestBuilders.post(url);
//        else requestBuilder = MockMvcRequestBuilders.get(url);
//        requestBuilder.contentType(MediaType.APPLICATION_JSON).content(params);
//        return requestBuilder;
//    }
//
//    private void performIdentityErrorTest(String url, String params, String displayName, boolean testBlocked, boolean isPost)
//    throws Exception {
//
//        System.out.println("Identity Error Test: " + displayName);
//        System.out.println(params);
//
//        ResultActions resultActions = mockMvc.perform(requestBuilder(url, params, isPost));
//
//        if (testBlocked) resultActions.andExpect(status().isBadRequest())
//                                      .andExpect(response -> assertTrue(response.getResolvedException() instanceof AccountBlockedException));
//        else resultActions.andExpect(status().isNotFound())
//                          .andExpect(response -> assertTrue(response.getResolvedException() instanceof AccountNotFoundException));
//    }
//
//    private void performFieldErrorTest(String url, String params, String displayName, boolean isPost)
//    throws Exception {
//
//        System.out.println("Field Error Test: " + displayName);
//        System.out.println(params);
//
//        mockMvc.perform(requestBuilder(url, params, isPost))
//               .andExpect(status().isBadRequest())
//               .andExpect(response -> assertTrue(response.getResolvedException() instanceof BadRequestException));
//    }
//}