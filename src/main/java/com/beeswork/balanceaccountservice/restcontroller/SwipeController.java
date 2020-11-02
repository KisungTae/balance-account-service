package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.constant.MessageKey;
import com.beeswork.balanceaccountservice.constant.NotificationType;
import com.beeswork.balanceaccountservice.dto.firebase.FCMNotificationDTO;
import com.beeswork.balanceaccountservice.dto.swipe.BalanceGameDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.projection.ClickedProjection;
import com.beeswork.balanceaccountservice.projection.MatchProjection;
import com.beeswork.balanceaccountservice.service.firebase.FCMService;
import com.beeswork.balanceaccountservice.service.swipe.SwipeService;
import com.beeswork.balanceaccountservice.vm.swipe.ClickVM;
import com.beeswork.balanceaccountservice.vm.swipe.ListClickedVM;
import com.beeswork.balanceaccountservice.vm.swipe.SwipeVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/swipe")
public class SwipeController extends BaseController {

    private final SwipeService swipeService;
    private final FCMService fcmService;
    private final MessageSource messageSource;

    @Autowired
    public SwipeController(ObjectMapper objectMapper, ModelMapper modelMapper, SwipeService swipeService, FCMService fcmService, MessageSource messageSource) {

        super(objectMapper, modelMapper);
        this.swipeService = swipeService;
        this.fcmService = fcmService;
        this.messageSource = messageSource;
    }

    @PostMapping
    public ResponseEntity<String> swipe(@Valid @RequestBody SwipeVM swipeVM, BindingResult bindingResult)
    throws JsonProcessingException {

        if (bindingResult.hasErrors()) throw new BadRequestException();

        BalanceGameDTO balanceGameDTO = swipeService.swipe(swipeVM.getAccountId(),
                                                           swipeVM.getEmail(),
                                                           swipeVM.getSwipedId());

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(balanceGameDTO));
    }

    @GetMapping("/clicked/list")
    public ResponseEntity<String> listClicked(@Valid @ModelAttribute ListClickedVM listClickedVM,
                                              BindingResult bindingResult)
    throws JsonProcessingException {

        if (bindingResult.hasErrors()) throw new BadRequestException();

        List<ClickedProjection> clickedProjections = swipeService.listClicked(listClickedVM.getAccountId(),
                                                                              listClickedVM.getEmail(),
                                                                              listClickedVM.getFetchedAt());

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(clickedProjections));
    }


    @PostMapping("/click")
    public ResponseEntity<String> click(@Valid @RequestBody ClickVM clickVM, BindingResult bindingResult, Locale locale)
    throws JsonProcessingException, FirebaseMessagingException {

        if (bindingResult.hasErrors()) throw new BadRequestException();

        ClickDTO clickDTO = swipeService.click(clickVM.getSwipeId(),
                                               clickVM.getAccountId(),
                                               clickVM.getEmail(),
                                               clickVM.getSwipedId(),
                                               clickVM.getAnswers());

        MatchProjection match = clickDTO.getMatch();
        if (clickDTO.getNotificationType().equals(NotificationType.MATCH)) {
            String message = messageSource.getMessage(MessageKey.MATCH_NOTIFICATION_MESSAGE, null, locale);
            fcmService.sendNotification(FCMNotificationDTO.matchNotification(clickDTO.getFcmToken(),
                                                                             match.getMatchedId().toString(),
                                                                             match.getName(),
                                                                             match.getChatId().toString(),
                                                                             match.getPhotoKey(),
                                                                             message));
        } else if (clickDTO.getNotificationType().equals(NotificationType.CLICK)) {
            String message = messageSource.getMessage(MessageKey.CLICKED_NOTIFICATION_MESSAGE, null, locale);
            String updatedAt = DateTimeFormatter.ISO_INSTANT.format(match.getUpdatedAt().toInstant());
            fcmService.sendNotification(FCMNotificationDTO.clickedNotification(clickDTO.getFcmToken(),
                                                                               match.getMatchedId().toString(),
                                                                               match.getPhotoKey(),
                                                                               updatedAt,
                                                                               message));
        }

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(clickDTO));
    }


}
