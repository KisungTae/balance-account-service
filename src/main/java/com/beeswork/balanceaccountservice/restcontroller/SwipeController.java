package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;
import com.beeswork.balanceaccountservice.dto.swipe.CountClicksDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ListClicksDTO;
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

    @PostMapping("/swipe")
    public ResponseEntity<String> swipe(@Valid @RequestBody SwipeVM swipeVM,
                                        BindingResult bindingResult,
                                        Principal principal)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        List<QuestionDTO> questionDTOs = swipeService.swipe(getAccountIdFrom(principal), swipeVM.getSwipedId());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(questionDTOs));
    }

    @GetMapping("/click/list")
    public ResponseEntity<String> listClicks(@Valid @ModelAttribute ListClicksVM listClicksVM,
                                             BindingResult bindingResult,
                                             Principal principal)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        ListClicksDTO listClicksDTO = swipeService.listClicks(getAccountIdFrom(principal),
                                                              listClicksVM.getStartPosition(),
                                                              listClicksVM.getLoadSize());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(listClicksDTO));
    }

    @GetMapping("/click/fetch")
    public ResponseEntity<String> fetchClicks(@Valid @ModelAttribute FetchClicksVM fetchClicksVM,
                                              BindingResult bindingResult,
                                              Principal principal)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        List<SwipeDTO> clicks = swipeService.fetchClicks(getAccountIdFrom(principal),
                                                         fetchClicksVM.getLastSwiperId(),
                                                         fetchClicksVM.getLoadSize());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(clicks));
    }

    @GetMapping("/click/count")
    public ResponseEntity<String> countClicks(Principal principal) throws JsonProcessingException {
        CountClicksDTO countClicksDTO = swipeService.countClicks(getAccountIdFrom(principal));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(countClicksDTO));
    }

    @PostMapping("/click")
    public ResponseEntity<String> click(@Valid @RequestBody ClickVM clickVM,
                                        BindingResult bindingResult,
                                        Locale locale,
                                        Principal principal)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        ClickDTO clickDTO = swipeService.click(getAccountIdFrom(principal), clickVM.getSwipedId(), clickVM.getAnswers());
        stompService.sendMatch(clickDTO.getObjMatchDTO(), locale);
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(clickDTO.getSubMatchDTO()));
    }
}
