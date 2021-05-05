package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.constant.PushType;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ListSwipesDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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
    public ResponseEntity<String> swipe(@Valid @RequestBody LikeVM likeVM, BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        List<QuestionDTO> questionDTOs = swipeService.swipe(likeVM.getAccountId(),
                                                            likeVM.getIdentityToken(),
                                                            likeVM.getSwipedId());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(questionDTOs));
    }

    @GetMapping("/swipe/list")
    public ResponseEntity<String> listSwipes(@Valid @ModelAttribute ListSwipesVM listSwipesVM,
                                             BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        ListSwipesDTO listSwipesDTO = swipeService.listSwipes(listSwipesVM.getAccountId(),
                                                              listSwipesVM.getIdentityToken(),
                                                              listSwipesVM.getFetchedAt());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(listSwipesDTO));
    }

    @GetMapping("/click/list")
    public ResponseEntity<String> listClicks(@Valid @ModelAttribute ListSwipesVM listSwipesVM,
                                             BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        List<SwipeDTO> clicks = swipeService.listClicks(listSwipesVM.getAccountId(),
                                                        listSwipesVM.getIdentityToken(),
                                                        listSwipesVM.getFetchedAt());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(clicks));
    }


    @PostMapping("/click")
    public ResponseEntity<String> click(@Valid @RequestBody ClickVM clickVM,
                                        BindingResult bindingResult,
                                        Locale locale)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
//        ClickDTO clickDTO = swipeService.click(clickVM.getAccountId(),
//                                               clickVM.getIdentityToken(),
//                                               clickVM.getSwipedId(),
//                                               clickVM.getAnswers());

        MatchDTO matchDTO = new MatchDTO();
        matchDTO.setSwiperId(clickVM.getAccountId());
        matchDTO.setSwipedId(UUID.randomUUID());
        matchDTO.setChatId(1L);
        matchDTO.setPushType(PushType.CLICKED);
        matchDTO.setName("testname");
        matchDTO.setProfilePhotoKey("testrepphotokey");
        matchDTO.setCreatedAt(new Date());

//        stompService.sendMatch(clickDTO.getObjMatchDTO(), locale);
        stompService.sendMatch(matchDTO, locale);
//        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(clickDTO.getSubMatchDTO()));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }
}
