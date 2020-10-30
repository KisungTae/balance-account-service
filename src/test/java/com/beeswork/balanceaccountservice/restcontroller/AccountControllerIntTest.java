package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.constant.ExceptionCode;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.QAccount;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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
    private ModelMapper modelMapper;

    @Autowired
    private MessageSource messageSource;

    private final QAccount qAccount = QAccount.account;

    @Test
    @DisplayName("saveProfileWithEmptyArguments")
    void saveProfileWithEmptyArguments() throws Exception {

        String params = getSaveProfileArgumentsAsJson("", null, "", "", null, "");
        MockHttpServletResponse response = mockMvc.perform(requestBuilder(URLPath.SAVE_PROFILE, params))
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

    @Test
    @DisplayName("saveProfileWithInvalidArguments")
    void saveProfileWithInvalidArguments() throws Exception {

        Map<String, String> arguments = getValidSaveProfileArgumentsAsMap("12322-32432-1232",
                                                                          getRandomString(Field.NAME_MAX + 1),
                                                                          "c2gmail.com",
                                                                          null,
                                                                          getRandomString(Field.ABOUT_MAX + 1),
                                                                          null);

        MockHttpServletResponse response = mockMvc.perform(requestBuilder(URLPath.SAVE_PROFILE, objectMapper.writeValueAsString(arguments)))
                                                  .andReturn()
                                                  .getResponse();

        Map content = objectMapper.readValue(response.getContentAsString(), Map.class);
        Map<String, String> fieldErrorMessages = (Map<String, String>) content.get(Field.FIELD_ERROR_MESSAGES);

        // check if error is fieldException
        assertEquals(ExceptionCode.FIELD_EXCEPTION, content.get(Field.ERROR));

        // check if the number of errors is 6
        assertEquals(2, fieldErrorMessages.size());

        // check if error messages are correct
        assertEquals(getErrorMessage(FieldError.NAME_LENGTH, new Object[] {Field.NAME_MIN, Field.NAME_MAX}), fieldErrorMessages.get(Field.NAME));
        assertEquals(getErrorMessage(FieldError.ABOUT_LENGTH), fieldErrorMessages.get(Field.ABOUT));
    }

    private Map<String, String> getValidSaveProfileArgumentsAsMap(String accountId, String name, String email, String birth, String about, String gender) {

        Account account = jpaQueryFactory.selectFrom(qAccount).fetchFirst();
        return getSaveProfileArgumentsAsMap(accountId == null ? account.getId().toString() : accountId,
                                            name == null ? "init test name" : name,
                                            email == null ? "initTest@gmail.com" : email,
                                            birth == null ? "2022-11-11" : birth,
                                            about == null ? "init test about" : about,
                                            gender == null ? "false" : gender);
    }

    private Map<String, String> getSaveProfileArgumentsAsMap(String accountId, String name, String email, String birth, String about, String gender) {

        Map<String, String> arguments = new HashMap<>();
        arguments.put(Field.ACCOUNT_ID, accountId);
        arguments.put(Field.NAME, name);
        arguments.put(Field.EMAIL, email);
        arguments.put(Field.BIRTH, birth);
        arguments.put(Field.ABOUT, about);
        arguments.put(Field.GENDER, gender);
        return arguments;
    }

    private String getSaveProfileArgumentsAsJson(String accountId, String name, String email, String birth, String about, String gender)
    throws JsonProcessingException {
        return objectMapper.writeValueAsString(getSaveProfileArgumentsAsMap(accountId, name, email, birth, about, gender));
    }

    private RequestBuilder requestBuilder(String url, String content) {
        return MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON).content(content);
    }

    private String getRandomString(int length) {
        return "d".repeat(Math.max(0, length));
    }

    private String getErrorMessage(String code) {
        return getErrorMessage(code, null);
    }

    private String getErrorMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, Locale.KOREA);
    }
}