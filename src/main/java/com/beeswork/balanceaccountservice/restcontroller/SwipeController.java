package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.*;
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
import java.security.Principal;
import java.util.*;

@RestController
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
    public ResponseEntity<String> like(@Valid @RequestBody LikeVM likeVM,
                                       BindingResult bindingResult,
                                       Principal principal,
                                       Locale locale)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        List<QuestionDTO> questionDTOs = swipeService.like(getAccountIdFrom(principal), likeVM.getSwipedId(), locale);
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(questionDTOs));
    }

    @GetMapping("/swipe/list")
    public ResponseEntity<String> listSwipes(@Valid @ModelAttribute ListSwipesVM listSwipesVM,
                                             BindingResult bindingResult,
                                             Principal principal)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        ListSwipesDTO listSwipesDTO = swipeService.listSwipes(getAccountIdFrom(principal),
                                                              listSwipesVM.getStartPosition(),
                                                              listSwipesVM.getLoadSize());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(listSwipesDTO));
    }

    @GetMapping("/swipe/fetch")
    public ResponseEntity<String> fetchSwipes(@Valid @ModelAttribute FetchSwipesVM fetchSwipesVM,
                                              BindingResult bindingResult,
                                              Principal principal)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        List<SwipeDTO> swipeDTOs = swipeService.fetchSwipes(getAccountIdFrom(principal),
                                                            fetchSwipesVM.getLastSwiperId(),
                                                            fetchSwipesVM.getLoadSize());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(swipeDTOs));
    }

    @GetMapping("/swipe/count")
    public ResponseEntity<String> countSwipes(Principal principal) throws JsonProcessingException {
        CountSwipesDTO countSwipesDTO = swipeService.countSwipes(getAccountIdFrom(principal));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(countSwipesDTO));
    }

    @PostMapping("/click")
    public ResponseEntity<String> click(@Valid @RequestBody ClickVM clickVM,
                                        BindingResult bindingResult,
                                        Locale locale,
                                        Principal principal)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        ClickDTO clickDTO = swipeService.click(getAccountIdFrom(principal), clickVM.getSwipedId(), clickVM.getAnswers(), locale);
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(clickDTO));
    }
}
