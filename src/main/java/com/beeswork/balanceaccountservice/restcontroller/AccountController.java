package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.account.DeleteAccountDTO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
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

    @PostMapping("/question/answers")
    public ResponseEntity<String> saveAnswers(@Valid @RequestBody SaveAnswersVM saveAnswersVM,
                                              BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        accountService.saveQuestionAnswers(saveAnswersVM.getAccountId(),
                                           saveAnswersVM.getIdentityToken(),
                                           saveAnswersVM.getAnswers());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @GetMapping("/question/list")
    public ResponseEntity<String> listQuestions(@Valid @ModelAttribute AccountIdentityVM accountIdentityVM,
                                                BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        List<QuestionDTO> questionDTOs = accountService.listQuestions(accountIdentityVM.getAccountId(),
                                                                      accountIdentityVM.getIdentityToken());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(questionDTOs));
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteAccount(@Valid @RequestBody AccountIdentityVM accountIdentityVM,
                                                BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        DeleteAccountDTO deleteAccountDTO = accountService.deleteAccount(accountIdentityVM.getAccountId(),
                                                                         accountIdentityVM.getIdentityToken());
//        s3Service.deletePhotosAsync(deleteAccountDTO.getAccountId(), deleteAccountDTO.getPhotoKeys());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }


}
