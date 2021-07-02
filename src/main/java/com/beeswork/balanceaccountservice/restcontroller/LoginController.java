package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.constant.LoginType;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.login.GoogleLoginService;
import com.beeswork.balanceaccountservice.service.login.LoginService;
import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import com.beeswork.balanceaccountservice.vm.account.SaveEmailVM;
import com.beeswork.balanceaccountservice.vm.login.LoginVM;
import com.beeswork.balanceaccountservice.vm.login.SocialLoginVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
public class LoginController extends BaseController {

    private final LoginService       loginService;
    private final GoogleLoginService googleLoginService;

    @Autowired
    public LoginController(ObjectMapper objectMapper,
                           ModelMapper modelMapper,
                           LoginService loginService, GoogleLoginService googleLoginService) {
        super(objectMapper, modelMapper);
        this.loginService = loginService;
        this.googleLoginService = googleLoginService;
    }

    @PostMapping("/login/email")
    public ResponseEntity<String> saveEmail(@Valid @RequestBody SaveEmailVM saveEmailVM,
                                            BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) super.fieldExceptionResponse(bindingResult);
        loginService.saveEmail(saveEmailVM.getAccountId(), saveEmailVM.getIdentityToken(), saveEmailVM.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @GetMapping("/login/email")
    public ResponseEntity<String> getEmail(@Valid @ModelAttribute AccountIdentityVM accountIdentityVM,
                                           BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        String email = loginService.getEmail(accountIdentityVM.getAccountId(), accountIdentityVM.getIdentityToken());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(email));
    }

    @PostMapping("/join")
    public ResponseEntity<String> join() {
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping("/login/social")
    public ResponseEntity<String> socialLogin(@Valid @RequestBody SocialLoginVM socialLoginVM,
                                              BindingResult bindingResult)
    throws GeneralSecurityException, IOException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        String loginId;
        switch (socialLoginVM.getLoginType()) {
            case GOOGLE: loginId = googleLoginService.validate(socialLoginVM.getLoginId(), socialLoginVM.getIdToken());
        }
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginVM loginVM,
                                        BindingResult bindingResult)
    throws GeneralSecurityException, IOException {
        if (bindingResult.hasErrors()) throw new BadRequestException();

        return ResponseEntity.status(HttpStatus.OK).body("");
    }
}
