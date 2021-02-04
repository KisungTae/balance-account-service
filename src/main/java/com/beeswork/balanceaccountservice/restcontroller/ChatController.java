package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.service.chat.ChatService;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.service.stomp.StompService;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import com.beeswork.balanceaccountservice.vm.chat.ListChatMessagesVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;

@RestController
public class ChatController {

    private final StompService stompService;
    private final ChatService  chatService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ChatController(StompService stompService,
                          ChatService chatService, ObjectMapper objectMapper) {
        this.stompService = stompService;
        this.chatService = chatService;
        this.objectMapper = objectMapper;
    }

    @MessageMapping("/chat/send")
    public void send(@Payload ChatMessageVM chatMessageVM, MessageHeaders messageHeaders) {
        stompService.send(chatMessageVM, messageHeaders);
    }

    @GetMapping("/chat/message/list")
    public ResponseEntity<String> listChatMessages(@Valid @ModelAttribute ListChatMessagesVM listChatMessagesVM,
                                                   BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        List<ChatMessageDTO> chatMessageDTOs = chatService.listChatMessages(listChatMessagesVM.getAccountId(),
                                                                            listChatMessagesVM.getIdentityToken(),
                                                                            listChatMessagesVM.getRecipientId(),
                                                                            listChatMessagesVM.getChatId(),
                                                                            listChatMessagesVM.getLastChatMessageCreatedAt());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(chatMessageDTOs));
    }

}


// maximum topic

// no subscriber channel, have to be handled manually,

// how @subscribe works

// how to check if there is subscriber discconected
