package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.login.LoginService;
import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import com.beeswork.balanceaccountservice.vm.login.SaveEmailVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class LoginController extends BaseController {

    private final LoginService loginService;

    @Autowired
    public LoginController(ObjectMapper objectMapper,
                           ModelMapper modelMapper,
                           LoginService loginService) {
        super(objectMapper, modelMapper);
        this.loginService = loginService;
    }

    @PostMapping("/join")
    public ResponseEntity<String> join() {


        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping("/login/google")
    public ResponseEntity<String> loginWithGoogle(String idToken) {

        return ResponseEntity.status(HttpStatus.OK).body("");
    }
}
