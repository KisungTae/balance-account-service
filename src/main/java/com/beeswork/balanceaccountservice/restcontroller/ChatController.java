package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.vm.ChatMessage;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ChatController {

    private final SimpMessagingTemplate template;
    private final Validator validator;

    @Autowired
    public ChatController(SimpMessagingTemplate template, Validator validator) {
        this.template = template;
        this.validator = validator;
    }



    public void sendChatMessage(@Valid @ModelAttribute ChatMessageVM chatMessageVM) {

    }

    public void subscribe() {

    }


    @MessageMapping("/broadcast")
//    @SendTo("/topic/messages")
    public void send(@Validated @Payload ChatMessage chatMessage) {




        if (chatMessage.getChatId() == 1)
            template.convertAndSend("/topic/messages/" + chatMessage.getChatId(), chatMessage);
//        return new ChatMessage(chatMessage.getFrom(), chatMessage.getText(), "ALL");
    }
}
