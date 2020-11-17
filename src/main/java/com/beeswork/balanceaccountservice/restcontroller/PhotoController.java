package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.photo.GenerateS3PreSignedURLDTO;
import com.beeswork.balanceaccountservice.service.photo.PhotoService;
import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
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
@RequestMapping("/photo")
public class PhotoController extends BaseController {

    private final PhotoService photoService;

    @Autowired
    public PhotoController(ObjectMapper objectMapper, ModelMapper modelMapper, PhotoService photoService) {
        super(objectMapper, modelMapper);
        this.photoService = photoService;
    }

    @PostMapping("/upload-url")
    public ResponseEntity<String> getUploadURL(@Valid @RequestBody AccountIdentityVM accountIdentityVM,
                                               BindingResult bindingResult)
    throws JsonProcessingException {

        if (bindingResult.hasErrors()) return super.fieldExceptionResponse(bindingResult);

        GenerateS3PreSignedURLDTO generateS3PreSignedURLDTO = photoService.generateS3PreSignedURL(accountIdentityVM.getAccountId(),
                                                                                                  accountIdentityVM.getIdentityToken());


        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(generateS3PreSignedURLDTO));
    }
}
