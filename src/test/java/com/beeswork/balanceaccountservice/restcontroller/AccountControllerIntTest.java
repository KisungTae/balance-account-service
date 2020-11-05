package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.constant.ExceptionCode;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.QAccount;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.exception.account.AccountBlockedException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private GeometryFactory geometryFactory;

    private final QAccount qAccount = QAccount.account;



    @Test
    @DisplayName("saveLocation_shouldUpdateLocationField")
    void saveLocation_shouldUpdateLocationField() throws Exception {

        Account account = jpaQueryFactory.selectFrom(qAccount).fetchFirst();
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put(Field.ACCOUNT_ID, account.getId());
        paramsMap.put(Field.EMAIL, account.getEmail());

        Random random = new Random();
        double latitude = 32.0 + random.nextDouble();
        double longitude = 122.0 + random.nextDouble();

        paramsMap.put(Field.LATITUDE, latitude);
        paramsMap.put(Field.LONGITUDE, longitude);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_LOCATION)
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .content(objectMapper.writeValueAsString(paramsMap));

        mockMvc.perform(requestBuilder).andExpect(status().isOk());

        Account account1 = jpaQueryFactory.selectFrom(qAccount).where(qAccount.id.eq(account.getId())).fetchOne();
        Point point = geometryFactory.createPoint(new Coordinate(latitude, longitude));
        assertEquals(account1.getLocation(), point);
    }



    @Test
    @DisplayName("saveLocationWithOutOfRange_shouldThrowBadRequestException")
    void saveLocationWithOutOfRange_shouldThrowBadRequestException() throws Exception {

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put(Field.ACCOUNT_ID, UUID.randomUUID());
        paramsMap.put(Field.EMAIL, "c0go2@naver.com");
        paramsMap.put(Field.LATITUDE, -180.235);
        paramsMap.put(Field.LONGITUDE, 270.14);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_LOCATION)
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .content(objectMapper.writeValueAsString(paramsMap));

        mockMvc.perform(requestBuilder)
               .andExpect(status().isBadRequest())
               .andExpect(response -> assertTrue(response.getResolvedException() instanceof BadRequestException));
    }

    @Test
    @DisplayName("saveLocationWithEmptyArguments_shouldThrowBadRequestException")
    void saveLocationWithEmptyArguments_shouldThrowBadRequestException() throws Exception {

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put(Field.ACCOUNT_ID, UUID.randomUUID());
        paramsMap.put(Field.EMAIL, "c0go2@naver.com");
        paramsMap.put(Field.LATITUDE, null);
        paramsMap.put(Field.LONGITUDE, null);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_LOCATION)
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .content(objectMapper.writeValueAsString(paramsMap));

        mockMvc.perform(requestBuilder)
               .andExpect(status().isBadRequest())
               .andExpect(response -> assertTrue(response.getResolvedException() instanceof BadRequestException));

    }



    @Test
    @DisplayName("saveLocationWithBlockedAccount_shouldThrowAccountBlockedException")
    void saveLocationWithBlockedAccount_shouldThrowAccountBlockedException() throws Exception {

        Account account = jpaQueryFactory.selectFrom(qAccount).where(qAccount.blocked.eq(true)).fetchFirst();

        Map<String, Object> paramsMap = getSaveLocationArguments(account.getId(), account.getEmail(), 45.2, 123.0);
        System.out.println("saveLocationWithBlockedAccount_shouldThrowAccountBlockedException - params");
        paramsMap.forEach((key, value) -> System.out.printf("%s=%s%n", key, value));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_LOCATION)
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .content(objectMapper.writeValueAsString(paramsMap));

        mockMvc.perform(requestBuilder)
               .andExpect(status().isBadRequest())
               .andExpect(response -> assertTrue(response.getResolvedException() instanceof AccountBlockedException));
    }

    @Test
    @DisplayName("saveLocationWithInvalidEmail_shouldThrowAccountNotFoundException")
    void saveLocationWithInvalidEmail_shouldThrowAccountNotFoundException() throws Exception {

        Account account = jpaQueryFactory.selectFrom(qAccount).where(qAccount.blocked.eq(false)).fetchFirst();

        Map<String, Object> paramsMap = getSaveLocationArguments(account.getId(), "abc" + account.getEmail(), 45.2, 123.0);
        System.out.println("saveLocationWithInvalidEmail_shouldThrowAccountNotFoundException - params");
        paramsMap.forEach((key, value) -> System.out.printf("%s=%s%n", key, value));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_LOCATION)
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .content(objectMapper.writeValueAsString(paramsMap));

        mockMvc.perform(requestBuilder)
               .andExpect(status().isNotFound())
               .andExpect(response -> assertTrue(response.getResolvedException() instanceof AccountNotFoundException));
    }


    @Test
    @DisplayName("saveLocationWithInvalidUUID_shouldThrowAccountNotFoundException")
    void saveLocationWithInvalidUUID_shouldThrowAccountNotFoundException() throws Exception {

        Map<String, Object> paramsMap = getSaveLocationArguments(UUID.randomUUID(), "c0go233@gmail.com", 45.2, 123.0);
        System.out.println("saveLocationWithInvalidUUID_shouldThrowAccountNotFoundException - params");
        paramsMap.forEach((key, value) -> System.out.printf("%s=%s%n", key, value));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_LOCATION)
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .content(objectMapper.writeValueAsString(paramsMap));

        mockMvc.perform(requestBuilder)
               .andExpect(status().isNotFound())
               .andExpect(response -> assertTrue(response.getResolvedException() instanceof AccountNotFoundException));
    }

    private Map<String, Object> getSaveLocationArguments(Object accountId, Object email, Object latitude, Object longitude) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put(Field.ACCOUNT_ID, accountId);
        paramsMap.put(Field.EMAIL, email);
        paramsMap.put(Field.LATITUDE, latitude);
        paramsMap.put(Field.LONGITUDE, longitude);
        return paramsMap;
    }



//  #################################################################################################  //
//  #################################################################################################  //
//  ################################      SaveProfile()      ########################################  //
//  #################################################################################################  //
//  #################################################################################################  //

    @Test
    @DisplayName("saveProfileWithAccountEnabled_shouldUpdatePartialFields")
    void saveProfileWithAccountEnabled_shouldUpdatePartialFields() throws Exception {

        Map paramsMap = getValidSaveProfileArgumentsAsMap(false, true);

        System.out.println("saveProfileWithAccountEnabled - params");
        paramsMap.forEach((key, value) -> System.out.printf("%s=%s%n", key, value));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_PROFILE)
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .content(objectMapper.writeValueAsString(paramsMap));

        mockMvc.perform(requestBuilder).andExpect(status().isOk());

        Account account = accountDAO.findById(UUID.fromString(paramsMap.get(Field.ACCOUNT_ID).toString()));

        assertNotEquals(paramsMap.get(Field.NAME).toString(), account.getName());
        assertNotEquals(Boolean.valueOf(paramsMap.get(Field.GENDER).toString()), account.isGender());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date birthParam = simpleDateFormat.parse(paramsMap.get(Field.BIRTH).toString());

        assertNotEquals(DateTimeFormatter.ISO_INSTANT.format(account.getBirth().toInstant()),
                        DateTimeFormatter.ISO_INSTANT.format(birthParam.toInstant()));

        assertEquals(account.getAbout(), paramsMap.get(Field.ABOUT));

        if (account.getHeight() != null)
            assertNotEquals(account.getHeight().toString(), paramsMap.get(Field.HEIGHT).toString());

    }

    @Test
    @DisplayName("saveProfileWithAccountDisabled_shouldUpdateAllFields")
    void saveProfileWithAccountDisabled_shouldUpdateAllFields() throws Exception {

        Map paramsMap = getValidSaveProfileArgumentsAsMap(false, false);

        System.out.println("saveProfileWithAccountDisabled - params");
        paramsMap.forEach((key, value) -> System.out.printf("%s=%s%n", key, value));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_PROFILE)
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .content(objectMapper.writeValueAsString(paramsMap));

        mockMvc.perform(requestBuilder).andExpect(status().isOk());

        Account account = accountDAO.findById(UUID.fromString(paramsMap.get(Field.ACCOUNT_ID).toString()));

        assertEquals(paramsMap.get(Field.NAME).toString(), account.getName());
        assertEquals(paramsMap.get(Field.ABOUT).toString(), account.getAbout());
        assertTrue(account.isEnabled());
        assertEquals(Boolean.valueOf(paramsMap.get(Field.GENDER).toString()), account.isGender());


        assertEquals(account.getHeight().toString(), paramsMap.get(Field.HEIGHT).toString());

    }

    //    @Test
    @DisplayName("saveProfileWithInvalidEmail_shouldThrowAccountNotFoundException")
    void saveProfileWithInvalidEmail_shouldThrowAccountNotFoundException() throws Exception {

        Map paramsMap = getValidSaveProfileArgumentsAsMap(false, true);
        paramsMap.put(Field.EMAIL, RandomStringGenerator.generate(10) + "@gmail.com");

        System.out.println("saveProfileWithInvalidEmail_shouldThrowAccountNotFoundException - params");
        paramsMap.forEach((key, value) -> System.out.printf("%s=%s%n", key, value));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_PROFILE)
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .content(objectMapper.writeValueAsString(paramsMap));

        mockMvc.perform(requestBuilder)
               .andExpect(status().isNotFound())
               .andExpect(response -> assertTrue(response.getResolvedException() instanceof AccountNotFoundException));
    }

    //    @Test
    @DisplayName("saveProfileWithBlockedAccount_shouldThrowAccountBlockedException")
    void saveProfileWithBlockedAccount_shouldThrowAccountBlockedException() throws Exception {

        Map paramsMap = getValidSaveProfileArgumentsAsMap(true, true);
        System.out.println("saveProfileWithBlockedAccount_shouldThrowAccountBlockedException - params");
        paramsMap.forEach((key, value) -> System.out.printf("%s=%s%n", key, value));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_PROFILE)
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .content(objectMapper.writeValueAsString(paramsMap));

        mockMvc.perform(requestBuilder)
               .andExpect(status().isBadRequest())
               .andExpect(response -> assertTrue(response.getResolvedException() instanceof AccountBlockedException));

    }

    //    @Test
    @DisplayName("saveProfileWithInvalidUUID_shouldThrowAccountNotFoundException")
    void saveProfileWithInvalidUUID_shouldThrowAccountNotFoundException() throws Exception {

        Map paramsMap = getValidSaveProfileArgumentsAsMap(false, true);
        paramsMap.put(Field.ACCOUNT_ID, UUID.randomUUID());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_PROFILE)
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .content(objectMapper.writeValueAsString(paramsMap));

        mockMvc.perform(requestBuilder)
               .andExpect(status().isNotFound())
               .andExpect(response -> assertTrue(response.getResolvedException() instanceof AccountNotFoundException));
    }

    //    @Test
    @DisplayName("saveProfileWithEmptyArguments")
    void saveProfileWithEmptyArguments_shouldThrowFieldException() throws Exception {

        Map paramsMap = getSaveProfileArgumentsAsMap("", null, "", "", null, "999", "");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_PROFILE)
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .content(objectMapper.writeValueAsString(paramsMap));

        MockHttpServletResponse response = mockMvc.perform(requestBuilder)
                                                  .andReturn()
                                                  .getResponse();

        Map content = objectMapper.readValue(response.getContentAsString(), Map.class);
        Map<String, String> fieldErrorMessages = (Map<String, String>) content.get(Field.FIELD_ERROR_MESSAGES);

        // check if error is fieldException
        assertEquals(ExceptionCode.FIELD_EXCEPTION, content.get(Field.ERROR));

        // check if the number of errors is 6
        assertEquals(6, fieldErrorMessages.size());

        // check if error messages are correct
        assertEquals(getErrorMessage(FieldError.UUID_EMPTY), fieldErrorMessages.get(Field.ACCOUNT_ID));
        assertEquals(getErrorMessage(FieldError.NAME_EMPTY), fieldErrorMessages.get(Field.NAME));
        assertEquals(getErrorMessage(FieldError.EMAIL_EMPTY), fieldErrorMessages.get(Field.EMAIL));
        assertEquals(getErrorMessage(FieldError.BIRTH_NULL), fieldErrorMessages.get(Field.BIRTH));
        assertEquals(getErrorMessage(FieldError.ABOUT_EMPTY), fieldErrorMessages.get(Field.ABOUT));
        assertEquals(getErrorMessage(FieldError.GENDER_NULL), fieldErrorMessages.get(Field.GENDER));
    }

    //    @Test
    @DisplayName("saveProfileWithInvalidArguments")
    void saveProfileWithInvalidArguments_shouldThrowFieldException() throws Exception {

        Map paramsMap = getSaveProfileArgumentsAsMap("12322-32432-1232",
                                                     RandomStringGenerator.generate(Field.NAME_MAX + 1),
                                                     "c2Gmail.com",
                                                     "2030-11-11",
                                                     RandomStringGenerator.generate(Field.ABOUT_MAX + 1),
                                                     "999",
                                                     "false");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URLPath.ACCOUNT_PROFILE)
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .content(objectMapper.writeValueAsString(paramsMap));

        MockHttpServletResponse response = mockMvc.perform(requestBuilder)
                                                  .andReturn()
                                                  .getResponse();

        Map content = objectMapper.readValue(response.getContentAsString(), Map.class);
        Map<String, String> fieldErrorMessages = (Map<String, String>) content.get(Field.FIELD_ERROR_MESSAGES);

        // check if error is fieldException
        assertEquals(ExceptionCode.FIELD_EXCEPTION, content.get(Field.ERROR));

        // check if the number of errors is 4
        assertEquals(4, fieldErrorMessages.size());

        // check if error messages are correct
        assertEquals(getErrorMessage(FieldError.UUID_INVALID), fieldErrorMessages.get(Field.ACCOUNT_ID));
        assertEquals(getErrorMessage(FieldError.EMAIL_INVALID), fieldErrorMessages.get(Field.EMAIL));

        String nameLengthErrorMessage = messageSource.getMessage(FieldError.NAME_LENGTH, null, Locale.KOREA);
        nameLengthErrorMessage = nameLengthErrorMessage.replaceAll("\\{min\\}", String.valueOf(Field.NAME_MIN));
        nameLengthErrorMessage = nameLengthErrorMessage.replaceAll("\\{max\\}", String.valueOf(Field.NAME_MAX));
        assertEquals(nameLengthErrorMessage, fieldErrorMessages.get(Field.NAME));

        String aboutLengthErrorMessage = messageSource.getMessage(FieldError.ABOUT_LENGTH, null, Locale.KOREA);
        aboutLengthErrorMessage = aboutLengthErrorMessage.replaceAll("\\{min\\}", String.valueOf(Field.ABOUT_MIN));
        aboutLengthErrorMessage = aboutLengthErrorMessage.replaceAll("\\{max\\}", String.valueOf(Field.ABOUT_MAX));
        assertEquals(aboutLengthErrorMessage, fieldErrorMessages.get(Field.ABOUT));

    }

    private Map<String, String> getValidSaveProfileArgumentsAsMap(boolean blocked, boolean enabled) {

        Account account = jpaQueryFactory.selectFrom(qAccount)
                                         .where(qAccount.blocked.eq(blocked).and(qAccount.enabled.eq(enabled)))
                                         .fetchFirst();

        return getSaveProfileArgumentsAsMap(account.getId().toString(),
                                            RandomStringGenerator.generate(10),
                                            account.getEmail(),
                                            "1910-01-01",
                                            RandomStringGenerator.generate(100),
                                            String.valueOf(999),
                                            String.valueOf((!account.isGender())));
    }

    private Map<String, String> getSaveProfileArgumentsAsMap(String accountId, String name, String email, String birth, String about, String height, String gender) {

        Map<String, String> arguments = new HashMap<>();
        arguments.put(Field.ACCOUNT_ID, accountId);
        arguments.put(Field.NAME, name);
        arguments.put(Field.EMAIL, email);
        arguments.put(Field.BIRTH, birth);
        arguments.put(Field.ABOUT, about);
        arguments.put(Field.HEIGHT, height);
        arguments.put(Field.GENDER, gender);
        return arguments;
    }

    private String getErrorMessage(String code) {
        return getErrorMessage(code, null);
    }

    private String getErrorMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, Locale.KOREA);
    }
}