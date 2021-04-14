package com.beeswork.balanceaccountservice.config.websocket;

import com.beeswork.balanceaccountservice.constant.RegexExpression;
import com.beeswork.balanceaccountservice.constant.StompHeader;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.service.chat.ChatService;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.lang.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;

import java.util.UUID;
import java.util.regex.Pattern;

public class StompChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private ChatService chatService;

    @Autowired
    private CompositeMessageConverter compositeMessageConverter;

    @Autowired
    private ObjectMapper objectMapper;


    private static final Pattern VALID_UUID_PATTERN = Pattern.compile(RegexExpression.VALID_UUID);
    private static final String FALSE = Boolean.toString(false);
    private static final String TRUE = Boolean.toString(true);


    //  NOTE 1. if exception is thrown from compositeMessageConverter.fromMessage for invalid UUId or Long then ignore the request
    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, @NonNull MessageChannel channel) {
        System.out.println("preSend!!!!!!!!!!!!");
        MessageHeaders messageHeaders = message.getHeaders();
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
        StompCommand stompCommand = stompHeaderAccessor.getCommand();

        System.out.println("stompCommand: " + stompCommand);
        Object destination = messageHeaders.get(StompHeader.SIMP_DESTINATION);
        System.out.println("destination: " + destination.toString());
//
//        if (StompCommand.SUBSCRIBE.equals(stompCommand)) {
//            String accountId = stompHeaderAccessor.getFirstNativeHeader(StompHeader.ACCOUNT_ID);
//            String identityToken = stompHeaderAccessor.getFirstNativeHeader(StompHeader.IDENTITY_TOKEN);
//            String recipientId = stompHeaderAccessor.getFirstNativeHeader(StompHeader.RECIPIENT_ID);
//            String chatId = stompHeaderAccessor.getFirstNativeHeader(StompHeader.CHAT_ID);
//            validateFields(accountId, identityToken, recipientId, chatId);
//            chatService.validateChat(accountId, identityToken, recipientId, chatId);
//
//            Object destination = messageHeaders.get(StompHeader.SIMP_DESTINATION);
//            if (destination == null || !queueName(accountId, chatId).equals(destination.toString()))
//                throw new BadRequestException();
//
//            if (!TRUE.equals(stompHeaderAccessor.getFirstNativeHeader(StompHeader.AUTO_DELETE)) ||
//                !FALSE.equals(stompHeaderAccessor.getFirstNativeHeader(StompHeader.EXCLUSIVE)) ||
//                !TRUE.equals(stompHeaderAccessor.getFirstNativeHeader(StompHeader.DURABLE)))
//                throw new BadRequestException();

//        } else if (StompCommand.SEND.equals(stompCommand)) {
//            ChatMessageVM chatMessageVM = (ChatMessageVM) compositeMessageConverter.fromMessage(message,
//                                                                                                ChatMessageVM.class);
//            String identityToken = stompHeaderAccessor.getFirstNativeHeader(StompHeader.IDENTITY_TOKEN);
//            String messageId = stompHeaderAccessor.getMessageId();
//            validateFields(chatMessageVM, identityToken, messageId);
//            checkValidUUID(identityToken);
//            checkValidNumber(messageId);
//            chatMessageVM.setId(chatService.saveChatMessage(chatMessageVM.getAccountId(),
//                                                            UUID.fromString(identityToken),
//                                                            chatMessageVM.getRecipientId(),
//                                                            chatMessageVM.getChatId(),
//                                                            Long.valueOf(messageId),
//                                                            chatMessageVM.getBody(),
//                                                            chatMessageVM.getCreatedAt()));
//            return MessageBuilder.createMessage(objectMapper.writeValueAsString(chatMessageVM),
//                                                stompHeaderAccessor.getMessageHeaders());
//        }
        return message;
    }

//    private void validateFields(ChatMessageVM chatMessageVM, String identityToken, String messageId) {
//        if (chatMessageVM == null) throw new BadRequestException();
//        checkValidUUID(identityToken);
//        validateFields(chatMessageVM.getAccountId(),
//                       identityToken,
//                       chatMessageVM.getRecipientId(),
//                       chatMessageVM.getChatId());
//        if (!isNumber(messageId)) throw new BadRequestException();
//    }

//    private void validateFields(String accountId, String identityToken, String recipientId, String chatId) {
//        checkValidUUID(accountId);
//        checkValidUUID(identityToken);
//        checkValidUUID(recipientId);
//        if (!isNumber(chatId)) throw new BadRequestException();
//    }

    private void checkValidUUID(String uuid) {
        if (uuid == null || !VALID_UUID_PATTERN.matcher(uuid).find())
            throw new BadRequestException();
    }

    private void checkValidNumber(String number) {
        if (number == null) {
            throw new BadRequestException();
        }
        int length = number.length();
        if (length == 0) {
            throw new BadRequestException();
        }
        int i = 0;
        if (number.charAt(0) == '-') {
            if (length == 1) {
                throw new BadRequestException();
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = number.charAt(i);
            if (c < '0' || c > '9') {
                throw new BadRequestException();
            }
        }
    }

    private String queueName(String accountId, String chatId) {
        return StompHeader.QUEUE_PREFIX + accountId + StompHeader.QUEUE_SEPARATOR + chatId;
    }

//    public static boolean isNumber(String str) {
//        if (str == null) {
//            return false;
//        }
//        int length = str.length();
//        if (length == 0) {
//            return false;
//        }
//        int i = 0;
//        if (str.charAt(0) == '-') {
//            if (length == 1) {
//                return false;
//            }
//            i = 1;
//        }
//        for (; i < length; i++) {
//            char c = str.charAt(i);
//            if (c < '0' || c > '9') {
//                return false;
//            }
//        }
//        return true;
//    }

}
