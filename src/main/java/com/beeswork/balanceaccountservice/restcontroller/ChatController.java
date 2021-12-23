package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.constant.StompHeader;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.chat.ListChatMessagesDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.chat.ChatService;
import com.beeswork.balanceaccountservice.service.stomp.StompService;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import com.beeswork.balanceaccountservice.vm.chat.FetchedChatMessageVM;
import com.beeswork.balanceaccountservice.vm.chat.ReceivedChatMessageVM;
import com.beeswork.balanceaccountservice.vm.chat.SyncChatMessagesVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Locale;

@RestController
@RequestMapping("/chat")
public class ChatController extends BaseController {

    private final StompService stompService;
    private final ChatService  chatService;

    @Autowired
    public ChatController(StompService stompService, ChatService chatService, ObjectMapper objectMapper, ModelMapper modelMapper) {
        super(objectMapper, modelMapper);
        this.stompService = stompService;
        this.chatService = chatService;
    }

    @MessageMapping("/chat/send")
    public void send(@Payload ChatMessageVM chatMessageVM, MessageHeaders messageHeaders) {
        if (chatMessageVM != null && !chatMessageVM.isError()) {
            Locale locale = StompHeader.getLocaleFromMessageHeaders(messageHeaders);
            stompService.sendChatMessage(modelMapper.map(chatMessageVM, ChatMessageDTO.class), locale);
        }
    }

    @PostMapping("/message/sync")
    public void syncChatMessages(@Valid @RequestBody SyncChatMessagesVM syncChatMessagesVM, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        chatService.syncChatMessages(syncChatMessagesVM.getSentChatMessageIds(), syncChatMessagesVM.getReceivedChatMessageIds());
    }

    @GetMapping("/message/list")
    public ResponseEntity<String> listChatMessages(BindingResult bindingResult, Principal principal)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        ListChatMessagesDTO listChatMessagesDTO = chatService.listChatMessages(getAccountIdFrom(principal));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(listChatMessagesDTO));
    }

    @PostMapping("/message/fetched")
    public ResponseEntity<String> fetchedChatMessage(@Valid @RequestBody FetchedChatMessageVM fetchedChatMessageVM,
                                                     BindingResult bindingResult,
                                                     Principal principal) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        chatService.fetchedChatMessage(getAccountIdFrom(principal), fetchedChatMessageVM.getChatMessageId());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/message/received")
    public ResponseEntity<String> receivedChatMessage(@Valid @RequestBody ReceivedChatMessageVM receivedChatMessageVM,
                                                     BindingResult bindingResult,
                                                     Principal principal) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        chatService.receivedChatMessage(getAccountIdFrom(principal), receivedChatMessageVM.getChatMessageId());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }


}


// maximum topic

// no subscriber channel, have to be handled manually,

// how @subscribe works

// how to check if there is subscriber discconected
