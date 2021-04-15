package com.beeswork.balanceaccountservice.config.websocket;

import com.beeswork.balanceaccountservice.constant.RegexExpression;
import com.beeswork.balanceaccountservice.constant.StompHeader;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.service.account.AccountService;
import com.beeswork.balanceaccountservice.service.chat.ChatService;
import com.beeswork.balanceaccountservice.service.stomp.StompService;
import com.beeswork.balanceaccountservice.util.Convert;
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
    private AccountService accountService;

    @Autowired
    private CompositeMessageConverter compositeMessageConverter;

    @Autowired
    private ObjectMapper objectMapper;


    //  NOTE 1. if exception is thrown from compositeMessageConverter.fromMessage for invalid UUId or Long then ignore the request
    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, @NonNull MessageChannel channel) {
        MessageHeaders messageHeaders = message.getHeaders();
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
        StompCommand stompCommand = stompHeaderAccessor.getCommand();

        if (StompCommand.SUBSCRIBE.equals(stompCommand)) validateBeforeSubscribe(stompHeaderAccessor);
        else if (StompCommand.SEND.equals(stompCommand)) {

        }
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

    private void validateBeforeSubscribe(StompHeaderAccessor stompHeaderAccessor) {
        BadRequestException badRequestException = new BadRequestException();
        String stringAccountId = stompHeaderAccessor.getFirstNativeHeader(StompHeader.ID);
        String stringIdentityToken = stompHeaderAccessor.getFirstNativeHeader(StompHeader.IDENTITY_TOKEN);
        UUID accountId = Convert.toUUIDOrThrow(stringAccountId, badRequestException);
        UUID identityToken = Convert.toUUIDOrThrow(stringIdentityToken, badRequestException);
        accountService.validateAccount(accountId, identityToken);

        String stringAutoDelete = stompHeaderAccessor.getFirstNativeHeader(StompHeader.AUTO_DELETE);
        String stringExclusive = stompHeaderAccessor.getFirstNativeHeader(StompHeader.EXCLUSIVE);
        String stringDurable = stompHeaderAccessor.getFirstNativeHeader(StompHeader.DURABLE);
        boolean autoDelete = Convert.toBooleanOrThrow(stringAutoDelete, badRequestException);
        boolean exclusive = Convert.toBooleanOrThrow(stringExclusive, badRequestException);
        boolean durable = Convert.toBooleanOrThrow(stringDurable, badRequestException);
        if (!autoDelete || exclusive || !durable) throw badRequestException;
    }

    private Message<?> saveAndSend(StompHeaderAccessor stompHeaderAccessor, Message<?> message) {
        BadRequestException badRequestException = new BadRequestException();
        ChatMessageVM chatMessageVM = (ChatMessageVM) compositeMessageConverter.fromMessage(message, ChatMessageVM.class);
        String stringAccountId = stompHeaderAccessor.getFirstNativeHeader(StompHeader.ID);
        String stringIdentityToken = stompHeaderAccessor.getFirstNativeHeader(StompHeader.IDENTITY_TOKEN);
        String stringRecipientId = stompHeaderAccessor.getFirstNativeHeader(StompHeader.RECIPIENT_ID);
        String stringChatId = stompHeaderAccessor.getFirstNativeHeader(StompHeader.CHAT_ID);

        UUID accountId = Convert.toUUIDOrThrow(stringAccountId, badRequestException);
        UUID identityToken = Convert.toUUIDOrThrow(stringIdentityToken, badRequestException);
        UUID recipientId = Convert.toUUIDOrThrow(stringRecipientId, badRequestException);
        long chatId = Convert.toLongOrThrow(stringChatId, badRequestException);
        long messageId = Convert.toLongOrThrow(stompHeaderAccessor.getMessageId(), badRequestException);
        String body =

        long chatMessageId = chatService.saveChatMessage(accountId, identityToken, recipientId, chatId, messageId, )


        validateFields(chatMessageVM, identityToken, messageId);
        checkValidUUID(identityToken);
        checkValidNumber(messageId);
        chatMessageVM.setId(chatService.saveChatMessage(chatMessageVM.getAccountId(),
                                                        UUID.fromString(identityToken),
                                                        chatMessageVM.getRecipientId(),
                                                        chatMessageVM.getChatId(),
                                                        Long.valueOf(messageId),
                                                        chatMessageVM.getBody(),
                                                        chatMessageVM.getCreatedAt()));
        return MessageBuilder.createMessage(objectMapper.writeValueAsString(chatMessageVM),
                                            stompHeaderAccessor.getMessageHeaders());
    }

    private void validateFields(String accountId, String identityToken) {

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
