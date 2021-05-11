package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.service.chat.ChatService;
import com.beeswork.balanceaccountservice.service.stomp.StompService;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import com.beeswork.balanceaccountservice.vm.chat.SyncChatMessagesVM;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final StompService stompService;
    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;

    @Autowired
    public ChatController(StompService stompService, ChatService chatService, ObjectMapper objectMapper, ModelMapper modelMapper) {
        this.stompService = stompService;
        this.chatService = chatService;
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
    }

    @MessageMapping("/chat/send")
    public void send(@Payload ChatMessageVM chatMessageVM, MessageHeaders messageHeaders) {
        stompService.sendChatMessage(modelMapper.map(chatMessageVM, ChatMessageDTO.class), messageHeaders);
    }

    @PostMapping("/message/sync")
    public void syncChatMessages(@Valid @RequestBody SyncChatMessagesVM syncChatMessagesVM,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        chatService.syncChatMessages(syncChatMessagesVM.getAccountId(),
                                     syncChatMessagesVM.getIdentityToken(),
                                     syncChatMessagesVM.getSentChatMessageIds(),
                                     syncChatMessagesVM.getReceivedChatMessageIds());
    }

}


// maximum topic

// no subscriber channel, have to be handled manually,

// how @subscribe works

// how to check if there is subscriber discconected
