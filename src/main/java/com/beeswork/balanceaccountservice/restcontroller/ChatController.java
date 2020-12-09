package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.vm.ChatMessage;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Set;

@RestController
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final SimpUserRegistry simpUserRegistry;


    @Autowired
    public ChatController(SimpMessagingTemplate simpMessagingTemplate, SimpUserRegistry simpUserRegistry) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.simpUserRegistry = simpUserRegistry;
    }

    @MessageMapping("/chat/send")
//    @SendTo("/topic/broadcast")
    public void send(@Payload ChatMessageVM chatMessageVM, SimpMessageHeaderAccessor simpMessageHeaderAccessor)
    throws Exception {

        simpMessagingTemplate.convertAndSend("/queue/" + 1, chatMessageVM);
    }
}


// maximum topic

// no subscriber channel, have to be handled manually,

// how @subscribe works

// how to check if there is subscriber discconected
