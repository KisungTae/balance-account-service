package com.beeswork.balanceaccountservice.config;

import com.beeswork.balanceaccountservice.constant.RegexExpression;
import com.beeswork.balanceaccountservice.constant.StompHeader;
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


    private static final Pattern VALID_UUID_PATTERN  = Pattern.compile(RegexExpression.VALID_UUID);
    private static final String FALSE = Boolean.toString(false);
    private static final String TRUE = Boolean.toString(true);


    @Override
    public Message<?> preSend(Message<?> message, @NonNull MessageChannel channel) {
        MessageHeaders messageHeaders = message.getHeaders();
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
        StompCommand stompCommand = stompHeaderAccessor.getCommand();

        if (StompCommand.SUBSCRIBE.equals(stompCommand)) {
//            String queue = validateChat(stompHeaderAccessor);
//            Object destination = messageHeaders.get(SIMP_DESTINATION);
//            if (destination == null || !queue.equals(destination.toString()))
//                throw new BadRequestException();

            if (!TRUE.equals(stompHeaderAccessor.getFirstNativeHeader(StompHeader.AUTO_DELETE)) ||
                !FALSE.equals(stompHeaderAccessor.getFirstNativeHeader(StompHeader.EXCLUSIVE)) ||
                !TRUE.equals(stompHeaderAccessor.getFirstNativeHeader(StompHeader.DURABLE)))
                throw new BadRequestException();
        } else if (StompCommand.SEND.equals(stompCommand)) {
//            validateChat(stompHeaderAccessor);
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

    private String validateChat(StompHeaderAccessor stompHeaderAccessor) {
        String accountId = stompHeaderAccessor.getFirstNativeHeader(StompHeader.ACCOUNT_ID);
        checkValidUUID(accountId);

        String identityToken = stompHeaderAccessor.getFirstNativeHeader(StompHeader.IDENTITY_TOKEN);
        checkValidUUID(identityToken);

        String matchedId = stompHeaderAccessor.getFirstNativeHeader(StompHeader.MATCHED_ID);
        checkValidUUID(matchedId);

        String chatId = stompHeaderAccessor.getFirstNativeHeader(StompHeader.CHAT_ID);
        if (!isNumber(chatId))
            throw new BadRequestException();

        chatService.checkIfValidChat(accountId, identityToken, matchedId, chatId);
        return queueName(accountId, chatId);
    }

    private void checkValidUUID(String uuid) {
        if (uuid == null || !VALID_UUID_PATTERN.matcher(uuid).find())
            throw new BadRequestException();
    }

    public static String queueName(String accountId, String chatId) {
        return StompHeader.QUEUE_PREFIX + accountId + StompHeader.QUEUE_SEPARATOR + chatId;
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
