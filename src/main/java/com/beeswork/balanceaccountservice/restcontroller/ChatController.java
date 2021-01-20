package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.service.chat.ChatService;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.service.stomp.StompService;
import com.beeswork.balanceaccountservice.vm.chat.ListMessagesVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ChatController {

    private final StompService stompService;
    private final ChatService chatService;

    @Autowired
    public ChatController(StompService stompService,
                          ChatService chatService) {
        this.stompService = stompService;
        this.chatService = chatService;
    }

    @MessageMapping("/chat/send")
    public void send(@Valid @Payload ChatMessageDTO chatMessageDTO, MessageHeaders messageHeaders) {
        stompService.send(chatMessageDTO, messageHeaders);
    }

    @GetMapping("/chat/message/list")
    public ResponseEntity<String> listMessages(@Valid @ModelAttribute ListMessagesVM listMessagesVM,
                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new BadRequestException();


        return ResponseEntity.status(HttpStatus.OK).body("");
    }

}


// maximum topic

// no subscriber channel, have to be handled manually,

// how @subscribe works

// how to check if there is subscriber discconected
