package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.firebase.FCMNotificationDTO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.projection.ClickedProjection;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.swipe.SwipeService;
import com.beeswork.balanceaccountservice.vm.swipe.SwipeClickVM;
import com.beeswork.balanceaccountservice.vm.swipe.SwipeClickedListVM;
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

@RestController
@RequestMapping("/swipe")
public class SwipeController extends BaseController {

    private final SwipeService swipeService;

    @Autowired
    public SwipeController(ObjectMapper objectMapper, ModelMapper modelMapper, SwipeService swipeService) {

        super(objectMapper, modelMapper);
        this.swipeService = swipeService;
    }

    @PostMapping
    public ResponseEntity<String> swipe(@Valid @RequestBody SwipeVM swipeVM, BindingResult bindingResult)
    throws JsonProcessingException {

        if (bindingResult.hasErrors()) throw new BadRequestException();

        List<QuestionDTO> questionDTOs = swipeService.swipe(swipeVM.getAccountId(),
                                                            swipeVM.getEmail(),
                                                            swipeVM.getSwipedId());

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(questionDTOs));
    }

    @GetMapping("/clicked/list")
    public ResponseEntity<String> listClicked(@Valid @ModelAttribute SwipeClickedListVM swipeClickedListVM,
                                              BindingResult bindingResult)
    throws JsonProcessingException {

        if (bindingResult.hasErrors()) throw new BadRequestException();

        List<ClickedProjection> clickedProjections = swipeService.listClicked(swipeClickedListVM.getAccountId(),
                                                                              swipeClickedListVM.getEmail(),
                                                                              swipeClickedListVM.getFetchedAt());

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(clickedProjections));
    }


    @PostMapping("/click")
    public ResponseEntity<String> click(@Valid @RequestBody SwipeClickVM swipeClickVM, BindingResult bindingResult)
    throws JsonProcessingException {

        if (bindingResult.hasErrors()) throw new BadRequestException();

        List<FCMNotificationDTO> FCMNotificationDTOs = swipeService.click(modelMapper.map(clickVM, ClickDTO.class));
        FCMService.sendNotifications(FCMNotificationDTOs);
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }


}
