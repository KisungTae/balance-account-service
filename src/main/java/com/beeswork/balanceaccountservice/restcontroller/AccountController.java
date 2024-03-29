package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.account.DeleteAccountDTO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.account.AccountService;
import com.beeswork.balanceaccountservice.service.login.LoginService;
import com.beeswork.balanceaccountservice.service.s3.S3Service;
import com.beeswork.balanceaccountservice.vm.account.*;
import com.beeswork.balanceaccountservice.vm.account.SaveEmailVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController extends BaseController {

    private final AccountService accountService;
    private final S3Service s3Service;

    @Autowired
    public AccountController(ObjectMapper objectMapper,
                             ModelMapper modelMapper,
                             AccountService accountService,
                             S3Service s3Service) {
        super(objectMapper, modelMapper);
        this.accountService = accountService;
        this.s3Service = s3Service;
    }


//  TODO: comment in s3Service.deletephoto
    @PostMapping("/delete")
    public ResponseEntity<String> deleteAccount(BindingResult bindingResult, Principal principal) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        DeleteAccountDTO deleteAccountDTO = accountService.deleteAccount(getAccountIdFrom(principal));
//        s3Service.deletePhotosAsync(deleteAccountDTO.getAccountId(), deleteAccountDTO.getPhotoKeys());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }


}
