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
@RequestMapping("/login")
public class LoginController extends BaseController {

    private final LoginService loginService;

    @Autowired
    public LoginController(ObjectMapper objectMapper,
                           ModelMapper modelMapper,
                           LoginService loginService) {
        super(objectMapper, modelMapper);
        this.loginService = loginService;
    }

    @PostMapping("/email")
    public ResponseEntity<String> saveEmail(@Valid @RequestBody SaveEmailVM saveEmailVM,
                                            BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) super.fieldExceptionResponse(bindingResult);
        loginService.saveEmail(saveEmailVM.getAccountId(), saveEmailVM.getIdentityToken(), saveEmailVM.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @GetMapping("/email")
    public ResponseEntity<String> getEmail(@Valid @ModelAttribute AccountIdentityVM accountIdentityVM,
                                           BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        String email = loginService.getEmail(accountIdentityVM.getAccountId(), accountIdentityVM.getIdentityToken());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(email));
    }
}
