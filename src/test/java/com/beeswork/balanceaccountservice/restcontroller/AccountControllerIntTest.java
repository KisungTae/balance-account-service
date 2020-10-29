package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.constant.ExceptionCode;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.QAccount;
import com.beeswork.balanceaccountservice.service.account.AccountService;
import com.beeswork.balanceaccountservice.vm.account.SaveProfileVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.internal.asm.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.persistence.EntityManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    private ModelMapper modelMapper;

    private final QAccount qAccount = QAccount.account;

    private final String ULR_SAVE_PROFILE = "/account/profile";


    @Test
    @DisplayName("saveProfileWithEmptyArguments")
    void saveProfileWithEmptyArguments() throws Exception {
        String params = getSaveProfileArgumentsAsJson("", "", "", "", "", "");
        MockHttpServletResponse response = mockMvc.perform(requestBuilder(ULR_SAVE_PROFILE, params))
                                                  .andReturn()
                                                  .getResponse();

        Map content = objectMapper.readValue(response.getContentAsString(), Map.class);
//        assertEquals(ExceptionCode.FIELD_EXCEPTION, content.get("errors"));
//        Map fieldErrors = objectMapper.readValue(content.get("fieldErrorMessages").toString(), Map.class);

        response.getContentAsString();
    }


    @Test
    @DisplayName("saveProfileWithInvalidArguments")
    void saveProfileWithInvalidArguments() throws Exception {


//        MockHttpServletResponse response = mockMvc.perform(requestBuilder(ULR_SAVE_PROFILE, params)).andReturn().getResponse();

    }

    private String getValidSaveProfileArgumentsAsJson() throws JsonProcessingException, ParseException {

        Account account = jpaQueryFactory.selectFrom(qAccount).fetchFirst();
        return getSaveProfileArgumentsAsJson(account.getId().toString(),
                                             account.getName(),
                                             account.getEmail(),
                                             "2022-11-11",
                                             account.getAbout(),
                                             Boolean.toString(account.isGender()));
    }

    private String getSaveProfileArgumentsAsJson(String accountId, String name, String email, String birth, String about, String gender)
    throws JsonProcessingException {

        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("accountId", accountId);
        requestParams.put("name", name);
        requestParams.put("email", email);
        requestParams.put("birth", birth);
        requestParams.put("about", about);
        requestParams.put("gender", gender);
        return objectMapper.writeValueAsString(requestParams);
    }


    private RequestBuilder requestBuilder(String url, String content) {
        return MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON).content(content);
    }


    @Test
    @DisplayName("POST /account/profile")
    void saveProfile() throws Exception {

//        String params = getValidSaveProfileArgumentsAsJson();
//
//        Map<String, Object> requestParams = new HashMap<>();
//        requestParams.put("accountId", "27a28b07-44f8-40af-813d-bf0e8db69010");
//        requestParams.put("name", "Michael Tae111");
//        requestParams.put("email", "");
//        requestParams.put("birth", "1987-12-10");
//        requestParams.put("about", "this is michel description1111");
//        requestParams.put("gender", false);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/account/profile")
//                                                              .contentType(MediaType.APPLICATION_JSON)
//                                                              .content(objectMapper.writeValueAsString(requestParams));
//
//        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
//
//        MockHttpServletResponse response = mvcResult.getResponse();
//
//        System.out.println("error message: " + response.getErrorMessage());
//        System.out.println("status: " + response.getStatus());
//        System.out.println("content: " + mvcResult.getResponse().getContentAsString());
    }
}