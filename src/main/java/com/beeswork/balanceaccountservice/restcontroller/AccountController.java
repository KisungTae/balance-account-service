package com.beeswork.balanceaccountservice.restcontroller;


import com.beeswork.balanceaccountservice.vm.AccountVM;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/account")
public class AccountController {



    @PostMapping("/save")
    public ResponseEntity<String> saveAccount(@Valid @RequestBody AccountVM accountVM,
                                              BindingResult bindingResult) {

        for (FieldError fieldError : bindingResult.getFieldErrors())
            System.out.println(fieldError.getField() + " : " + fieldError.getDefaultMessage());

        System.out.println(accountVM);
        return ResponseEntity.status(HttpStatus.OK).body("");
    }
}
