package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.photo.PhotoDTO;
import com.beeswork.balanceaccountservice.dto.s3.PreSignedUrl;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.photo.PhotoService;
import com.beeswork.balanceaccountservice.service.s3.S3Service;
import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import com.beeswork.balanceaccountservice.vm.photo.AddPhotoVM;
import com.beeswork.balanceaccountservice.vm.photo.DeletePhotoVM;
import com.beeswork.balanceaccountservice.vm.photo.ReorderPhotosVM;
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
@RequestMapping("/photo")
public class PhotoController extends BaseController {

    private final PhotoService photoService;
    private final S3Service    s3Service;

    @Autowired
    public PhotoController(ObjectMapper objectMapper, ModelMapper modelMapper, PhotoService photoService,
                           S3Service s3Service) {
        super(objectMapper, modelMapper);
        this.photoService = photoService;
        this.s3Service = s3Service;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addPhoto(@Valid @RequestBody AddPhotoVM addPhotoVM,
                                           BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        photoService.addPhoto(addPhotoVM.getAccountId(),
                              addPhotoVM.getIdentityToken(),
                              addPhotoVM.getPhotoKey(),
                              addPhotoVM.getSequence());

        PreSignedUrl preSignedUrl = s3Service.preSignedUrl(addPhotoVM.getAccountId().toString(), addPhotoVM.getPhotoKey());

        return ResponseEntity.status(HttpStatus.OK)
                             .body(objectMapper.writeValueAsString(preSignedUrl));
    }

    @GetMapping("/list")
    public ResponseEntity<String> listPhotos(@Valid @ModelAttribute AccountIdentityVM accountIdentityVM,
                                             BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        List<PhotoDTO> photoDTOs = photoService.listPhotos(accountIdentityVM.getAccountId(),
                                                           accountIdentityVM.getIdentityToken());

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(photoDTOs));
    }

    @PostMapping("/reorder")
    public ResponseEntity<String> reorderPhotos(@Valid @RequestBody ReorderPhotosVM reorderPhotosVM,
                                                BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();

        photoService.reorderPhotos(reorderPhotosVM.getAccountId(),
                                   reorderPhotosVM.getIdentityToken(),
                                   reorderPhotosVM.getPhotoOrders());

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deletePhoto(@Valid @RequestBody DeletePhotoVM deletePhotoVM,
                                              BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();

        photoService.deletePhoto(deletePhotoVM.getAccountId(),
                                 deletePhotoVM.getIdentityToken(),
                                 deletePhotoVM.getPhotoKey());

        s3Service.deletePhoto(deletePhotoVM.getAccountId().toString(), deletePhotoVM.getPhotoKey());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }
}
