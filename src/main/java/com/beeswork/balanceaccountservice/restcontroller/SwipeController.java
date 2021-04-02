package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ListClickedDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.service.stomp.StompService;
import com.beeswork.balanceaccountservice.service.swipe.SwipeService;
import com.beeswork.balanceaccountservice.vm.swipe.*;
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
    private final StompService stompService;

    @Autowired
    public SwipeController(ObjectMapper objectMapper,
                           ModelMapper modelMapper,
                           SwipeService swipeService,
                           StompService stompService) {

        super(objectMapper, modelMapper);
        this.swipeService = swipeService;
        this.stompService = stompService;
    }

    @PostMapping("/like")
    public ResponseEntity<String> like(@Valid @RequestBody LikeVM likeVM, BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        List<QuestionDTO> questionDTOs = swipeService.like(likeVM.getAccountId(),
                                                           likeVM.getIdentityToken(),
                                                           likeVM.getSwipedId());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(questionDTOs));
    }

    @GetMapping("/clicked/list")
    public ResponseEntity<String> listSwipes(@Valid @ModelAttribute ListSwipesVM listSwipesVM,
                                             BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        ListClickedDTO listClickedDTO = swipeService.listClicked(listSwipesVM.getAccountId(),
                                                                 listSwipesVM.getIdentityToken(),
                                                                 listSwipesVM.getFetchedAt());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(listClickedDTO));
    }

    @GetMapping("/clicker/list")
    public ResponseEntity<String> listClickers(@Valid @ModelAttribute ListSwipesVM listSwipesVM,
                                               BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        List<SwipeDTO> clickers = swipeService.listClickers(listSwipesVM.getAccountId(),
                                                            listSwipesVM.getIdentityToken(),
                                                            listSwipesVM.getFetchedAt());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(clickers));
    }



    @PostMapping("/click")
    public ResponseEntity<String> click(@Valid @RequestBody ClickVM clickVM,
                                        BindingResult bindingResult,
                                        Locale locale)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        ClickDTO clickDTO = swipeService.click(clickVM.getAccountId(),
                                               clickVM.getIdentityToken(),
                                               clickVM.getSwipedId(),
                                               clickVM.getAnswers());
        stompService.sendPush(clickDTO.getClickedPush(), locale);
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(clickDTO.getClickerPush()));
    }
}
