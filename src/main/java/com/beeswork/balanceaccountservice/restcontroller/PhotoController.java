package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.photo.PhotoDTO;
import com.beeswork.balanceaccountservice.dto.s3.PreSignedUrl;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.photo.PhotoService;
import com.beeswork.balanceaccountservice.service.s3.S3Service;
import com.beeswork.balanceaccountservice.vm.photo.SavePhotoVM;
import com.beeswork.balanceaccountservice.vm.photo.DeletePhotoVM;
import com.beeswork.balanceaccountservice.vm.photo.GeneratePreSignedURLVM;
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
import java.security.Principal;
import java.util.List;
import java.util.UUID;

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

    @GetMapping("/sign")
    public ResponseEntity<String> generatePreSignedURL(@Valid @ModelAttribute GeneratePreSignedURLVM generatePreSignedURLVM,
                                                       BindingResult bindingResult,
                                                       Principal principal) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        PreSignedUrl preSignedUrl = s3Service.generatePreSignedUrl(getAccountIdFrom(principal),
                                                                   generatePreSignedURLVM.getPhotoKey());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(preSignedUrl));
    }

    @PostMapping("/save")
    public ResponseEntity<String> savePhoto(@Valid @RequestBody SavePhotoVM savePhotoVM,
                                            BindingResult bindingResult,
                                            Principal principal)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        photoService.savePhoto(getAccountIdFrom(principal), savePhotoVM.getPhotoKey(), savePhotoVM.getSequence());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @GetMapping("/list")
    public ResponseEntity<String> listPhotos(BindingResult bindingResult, Principal principal)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        List<PhotoDTO> photoDTOs = photoService.listPhotos(getAccountIdFrom(principal));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(photoDTOs));
    }

    @PostMapping("/reorder")
    public ResponseEntity<String> reorderPhotos(@Valid @RequestBody ReorderPhotosVM reorderPhotosVM,
                                                BindingResult bindingResult,
                                                Principal principal) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        photoService.reorderPhotos(getAccountIdFrom(principal), reorderPhotosVM.getPhotoOrders());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deletePhoto(@Valid @RequestBody DeletePhotoVM deletePhotoVM,
                                              BindingResult bindingResult,
                                              Principal principal) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        UUID accountId = getAccountIdFrom(principal);
        photoService.deletePhoto(accountId, deletePhotoVM.getPhotoKey());
        s3Service.deletePhoto(accountId, deletePhotoVM.getPhotoKey());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }
}
