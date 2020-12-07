package com.beeswork.balanceaccountservice.config;

import com.beeswork.balanceaccountservice.service.chat.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public class ChatSubscriptionInterceptor implements ChannelInterceptor {

    @Autowired
    private ChatService chatService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        MessageHeaders headers = message.getHeaders();
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {

            Principal userPrincipal = headerAccessor.getUser();

            MultiValueMap<String, String> multiValueMap = headers.get(StompHeaderAccessor.NATIVE_HEADERS,
                                                                      MultiValueMap.class);

            for (Map.Entry<String, List<String>> entry : multiValueMap.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }

            if (!validateSubscription(userPrincipal, headerAccessor.getDestination())) {
                throw new IllegalArgumentException("No permission for this topic");
            }
        }
        return message;
    }

    private boolean validateSubscription(Principal principal, String topicDestination) {


        chatService.checkIfValidMatch("aa", "dd", "dd", 1L);
        if (principal == null) {
            // unauthenticated user
            return false;
        }
        //Additional validation logic coming here
        return true;
    }


}
