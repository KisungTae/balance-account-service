package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.service.account.AccountService;
import com.beeswork.balanceaccountservice.vm.account.SaveProfileVM;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
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

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

//@ExtendWith(SpringExtension.class)
//@WebMvcTest(AccountController.class)
//@ContextConfiguration(classes = {ModelMapper.class})
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerIntTest {

    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /account/profile")
    void saveProfile() throws Exception {

        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("accountId", "27a28b07-44f8-40af-813d-bf0e8db69010");
        requestParams.put("name", "Michael Tae111");
        requestParams.put("email", "0@gmail.com");
        requestParams.put("birth", "1987-12-10");
        requestParams.put("about", "this is michel description1111");
        requestParams.put("gender", false);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/account/profile")
                                                              .content(MediaType.APPLICATION_JSON_VALUE)
                                                              .content(objectMapper.writeValueAsString(requestParams));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        System.out.println("error message: " + response.getErrorMessage());
        System.out.println("status: " + response.getStatus());
        System.out.println("content: " + mvcResult.getResponse().getContentAsString());
    }
//
//    @Autowired
//    private ObjectMapper objectMapper;

//    @Test
//    void saveProfile() throws Exception {
//
//        Map<String, Object> requestParams = new HashMap<>();
//        requestParams.put("accountId", "27a28b07-44f8-40af-813d-bf0e8db69010");
//        requestParams.put("name", "Michael Tae111");
//        requestParams.put("email", "0@gmail.com");
//        requestParams.put("birth", "1987-12-10");
//        requestParams.put("about", "this is michel description1111");
//        requestParams.put("gender", false);
//
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/account/profile")
//                                                              .content(MediaType.APPLICATION_JSON_VALUE)
//                                                              .content(objectMapper.writeValueAsString(requestParams));
//
//        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
//
//
//        MockHttpServletResponse response = mvcResult.getResponse();
//
//        System.out.println("error message: " + response.getErrorMessage());
//        System.out.println("status: " + response.getStatus());
//        System.out.println("content: " + mvcResult.getResponse().getContentAsString());
//
//        assertEquals("", mvcResult.getResponse().getContentAsString());
//    }
//
//    public static String asJsonString(final Object obj) {
//        try {
//            return new ObjectMapper().writeValueAsString(obj);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

}