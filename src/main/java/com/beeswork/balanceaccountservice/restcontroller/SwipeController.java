package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.swipe.BalanceGameDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.projection.ClickProjection;
import com.beeswork.balanceaccountservice.projection.ClickedProjection;
import com.beeswork.balanceaccountservice.service.firebase.FirebaseService;
import com.beeswork.balanceaccountservice.service.swipe.SwipeService;
import com.beeswork.balanceaccountservice.vm.swipe.ClickVM;
import com.beeswork.balanceaccountservice.vm.swipe.ListClickVM;
import com.beeswork.balanceaccountservice.vm.swipe.ListClickedVM;
import com.beeswork.balanceaccountservice.vm.swipe.SwipeVM;
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
import java.util.Locale;

@RestController
@RequestMapping("/swipe")
public class SwipeController extends BaseController {

    private final SwipeService swipeService;
    private final FirebaseService firebaseService;

    @Autowired
    public SwipeController(ObjectMapper objectMapper, ModelMapper modelMapper, SwipeService swipeService, FirebaseService firebaseService) {

        super(objectMapper, modelMapper);
        this.swipeService = swipeService;
        this.firebaseService = firebaseService;
    }

    @PostMapping
    public ResponseEntity<String> swipe(@Valid @RequestBody SwipeVM swipeVM, BindingResult bindingResult)
    throws JsonProcessingException {

        if (bindingResult.hasErrors()) throw new BadRequestException();

        BalanceGameDTO balanceGameDTO = swipeService.swipe(swipeVM.getAccountId(),
                                                           swipeVM.getIdentityToken(),
                                                           swipeVM.getSwipeId(),
                                                           swipeVM.getSwipedId());

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(balanceGameDTO));
    }

    @GetMapping("/click/list")
    public ResponseEntity<String> listClick(@Valid @ModelAttribute ListClickVM listClickVM,
                                            BindingResult bindingResult) throws JsonProcessingException {

        if (bindingResult.hasErrors()) throw new BadRequestException();

        List<ClickProjection> clickProjections = swipeService.listClick(listClickVM.getAccountId(),
                                                                        listClickVM.getIdentityToken(),
                                                                        listClickVM.getFetchedAt());

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(clickProjections));
    }

    @GetMapping("/clicked/list")
    public ResponseEntity<String> listClicked(@Valid @ModelAttribute ListClickedVM listClickedVM,
                                              BindingResult bindingResult)
    throws JsonProcessingException {

        if (bindingResult.hasErrors()) throw new BadRequestException();

        List<ClickedProjection> clickedProjections = swipeService.listClicked(listClickedVM.getAccountId(),
                                                                              listClickedVM.getIdentityToken(),
                                                                              listClickedVM.getFetchedAt());

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(clickedProjections));
    }


    @PostMapping("/click")
    public ResponseEntity<String> click(@Valid @RequestBody ClickVM clickVM,
                                        BindingResult bindingResult,
                                        Locale locale)
    throws JsonProcessingException {

        if (bindingResult.hasErrors()) throw new BadRequestException();

        ClickDTO clickDTO = swipeService.click(clickVM.getSwipeId(),
                                               clickVM.getAccountId(),
                                               clickVM.getIdentityToken(),
                                               clickVM.getSwipedId(),
                                               clickVM.getAnswers());

        firebaseService.sendNotification(clickDTO, locale);
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(clickDTO));
    }
}
