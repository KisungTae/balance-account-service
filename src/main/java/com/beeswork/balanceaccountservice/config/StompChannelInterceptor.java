package com.beeswork.balanceaccountservice.config;

import com.beeswork.balanceaccountservice.constant.RegexExpression;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.exception.match.MatchNotFoundException;
import com.beeswork.balanceaccountservice.service.chat.ChatService;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import com.google.api.gax.rpc.InvalidArgumentException;
import io.micrometer.core.lang.NonNull;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class StompChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private ChatService chatService;

    @Autowired
    private CompositeMessageConverter compositeMessageConverter;

    private static final String ACCOUNT_ID       = "accountId";
    private static final String IDENTITY_TOKEN   = "identityToken";
    private static final String CHAT_ID          = "chatId";
    private static final String AUTO_DELETE      = "auto-delete";
    private static final String EXCLUSIVE        = "exclusive";
    private static final String DURABLE          = "durable";
    private static final String SIMP_DESTINATION = "simpDestination";
    private static final String QUEUE_PREFIX     = "/queue/";
    private static final String QUEUE_SEPARATOR  = "-";


    @Override
    public Message<?> preSend(Message<?> message, @NonNull MessageChannel channel) {
        MessageHeaders messageHeaders = message.getHeaders();
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
        StompCommand stompCommand = stompHeaderAccessor.getCommand();



        if (StompCommand.SUBSCRIBE.equals(stompCommand)) {
//            validateChat(stompHeaderAccessor, stompCommand, messageHeaders.get(SIMP_DESTINATION));


//            TODO: check these three headers
//            String autoDelete = stompHeaderAccessor.getFirstNativeHeader(AUTO_DELETE);
//            String exclusive = stompHeaderAccessor.getFirstNativeHeader(EXCLUSIVE);
//            String durable = stompHeaderAccessor.getFirstNativeHeader(DURABLE);
//
//            if (autoDelete == null || autoDelete == Boolean.to || exclusive == null || exclusive || durable == null || !durable)
//                throw new BadRequestException();


//            stompHeaderAccessor.addNativeHeader(AUTO_DELETE, Boolean.toString(true));
//            stompHeaderAccessor.addNativeHeader(EXCLUSIVE, Boolean.toString(false));
//            stompHeaderAccessor.addNativeHeader(DURABLE, Boolean.toString(true));
        } else if (StompCommand.SEND.equals(stompCommand)) {
//            validateChat(stompHeaderAccessor, stompCommand, messageHeaders.get(SIMP_DESTINATION));
        }

//        for (Map.Entry<String, List<String>> entry : nativeHeaders.entrySet()) {
//            System.out.println(entry.getKey() + ": " + entry.getValue());
//        }

//        if (StompCommand.SEND.equals(stompHeaderAccessor.getCommand())) {
//            ChatMessageVM chatMessageVM =
//                    (ChatMessageVM) compositeMessageConverter.fromMessage(message, ChatMessageVM.class);
//            if (chatMessageVM == null) System.out.println("chatMessageVM == null");
//            else {
//                System.out.println("payload message: " + chatMessageVM.getMessage());
//                if (chatMessageVM.getMessage().equals("abc"))
//                    throw new BadRequestException();
//            }
//        }

//        if (StompCommand.ACK.equals(stompHeaderAccessor.getCommand())) {
//
//        }


//        if (StompCommand.SUBSCRIBE.equals(stompHeaderAccessor.getCommand())) {
//            Principal userPrincipal = stompHeaderAccessor.getUser();


//            for (Map.Entry<String, List<String>> entry : nativeHeaders.entrySet()) {
//                System.out.println(entry.getKey() + ": " + entry.getValue());
//            }

//            if (!validateSubscription(userPrincipal, stompHeaderAccessor.getDestination())) {
//                throw new IllegalArgumentException("No permission for this topic");
//            }
//        }
        return message;
    }

    private void validateChat(StompHeaderAccessor stompHeaderAccessor, StompCommand stompCommand, Object destination) {
        Pattern uuidPattern = Pattern.compile(RegexExpression.VALID_UUID);

        String accountId = stompHeaderAccessor.getFirstNativeHeader(ACCOUNT_ID);
        if (accountId == null || !uuidPattern.matcher(accountId).find())
            throw new BadRequestException();

        String identityToken = stompHeaderAccessor.getFirstNativeHeader(IDENTITY_TOKEN);
        if (identityToken == null || !uuidPattern.matcher(identityToken).find())
            throw new BadRequestException();

        String chatId = stompHeaderAccessor.getFirstNativeHeader(CHAT_ID);
        if (!isNumber(chatId))
            throw new BadRequestException();

        if (destination == null)
            throw new BadRequestException();

        String matchedId = chatService.checkIfValidChat(accountId, identityToken, chatId);
        if (StompCommand.SEND.equals(stompCommand))
            accountId = matchedId;

        String queue = QUEUE_PREFIX + accountId + QUEUE_SEPARATOR + chatId;
        // TODO: change to !queue.equals
        if (queue.equals(destination.toString()))
            throw new BadRequestException();
    }

    public static boolean isNumber(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }





}
