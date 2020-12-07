package com.beeswork.balanceaccountservice.controller;


import com.beeswork.balanceaccountservice.vm.ChatMessage;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebSocketBroadcastController {

    @GetMapping("/stomp-broadcast")
    public String getWebSocketBroadcast() {
//        return "ddd";
        return "stomp-broadcast";
    }


}
