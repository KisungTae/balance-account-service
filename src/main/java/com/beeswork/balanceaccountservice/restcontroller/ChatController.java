package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.service.chat.ChatService;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.service.stomp.StompService;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import com.beeswork.balanceaccountservice.vm.chat.ListChatMessagesVM;
import com.beeswork.balanceaccountservice.vm.chat.SyncChatMessagesVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final StompService stompService;
    private final ChatService  chatService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ChatController(StompService stompService, ChatService chatService, ObjectMapper objectMapper) {
        this.stompService = stompService;
        this.chatService = chatService;
        this.objectMapper = objectMapper;
    }

    @MessageMapping("/send")
    public void send(@Payload ChatMessageVM chatMessageVM, MessageHeaders messageHeaders) {
        stompService.send(chatMessageVM, messageHeaders);
    }

    @PostMapping("/message/sync")
    public void syncChatMessages(@Valid @RequestBody SyncChatMessagesVM syncChatMessagesVM,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        chatService.syncChatMessages(syncChatMessagesVM.getAccountId(),
                                     syncChatMessagesVM.getIdentityToken(),
                                     syncChatMessagesVM.getChatMessageIds());
    }

    //  TODO: remove me
    @PostMapping("/message/save")
    public void saveChatMessage(@RequestBody ChatMessageVM chatMessageVM) {
        ChatMessageDTO chatMessageDTO = chatService.saveChatMessage(chatMessageVM.getAccountId(),
                                                                    chatMessageVM.getAccountId(),
                                                                    chatMessageVM.getRecipientId(),
                                                                    chatMessageVM.getChatId(),
                                                                    chatMessageVM.getMessageId(),
                                                                    chatMessageVM.getBody(),
                                                                    chatMessageVM.getCreatedAt());
        System.out.println("chat message created");
        System.out.println(chatMessageDTO.getId());
        System.out.println(chatMessageDTO.getCreatedAt());
    }

}


// maximum topic

// no subscriber channel, have to be handled manually,

// how @subscribe works

// how to check if there is subscriber discconected
