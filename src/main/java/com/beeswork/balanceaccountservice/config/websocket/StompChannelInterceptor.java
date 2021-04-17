package com.beeswork.balanceaccountservice.config.websocket;

import com.beeswork.balanceaccountservice.constant.RegexExpression;
import com.beeswork.balanceaccountservice.constant.StompHeader;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.service.account.AccountService;
import com.beeswork.balanceaccountservice.service.chat.ChatService;
import com.beeswork.balanceaccountservice.service.stomp.StompService;
import com.beeswork.balanceaccountservice.util.Convert;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.concurrent.ExecutionException;
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

        System.out.println("presend");
        if (StompCommand.SUBSCRIBE.equals(stompCommand)) validateBeforeSubscribe(stompHeaderAccessor, messageHeaders);
//        else if (StompCommand.SEND.equals(stompCommand)) return saveAndSend(stompHeaderAccessor, message, messageHeaders);
        return message;
    }

    private void validateBeforeSubscribe(StompHeaderAccessor stompHeaderAccessor, MessageHeaders messageHeaders) {
        BadRequestException badRequestException = new BadRequestException();
        UUID accountId = Convert.toUUIDOrThrow(stompHeaderAccessor.getFirstNativeHeader(StompHeader.ID), badRequestException);
        UUID identityToken = Convert.toUUIDOrThrow(stompHeaderAccessor.getFirstNativeHeader(StompHeader.IDENTITY_TOKEN), badRequestException);
        accountService.validateAccount(accountId, identityToken);
        validateDestination(messageHeaders.get(StompHeader.SIMP_DESTINATION), accountId);

        boolean autoDelete = Convert.toBooleanOrThrow(stompHeaderAccessor.getFirstNativeHeader(StompHeader.AUTO_DELETE), badRequestException);
        boolean exclusive = Convert.toBooleanOrThrow(stompHeaderAccessor.getFirstNativeHeader(StompHeader.EXCLUSIVE), badRequestException);
        boolean durable = Convert.toBooleanOrThrow(stompHeaderAccessor.getFirstNativeHeader(StompHeader.DURABLE), badRequestException);
        if (!autoDelete || exclusive || !durable) throw badRequestException;
    }

    private Message<?> saveAndSend(StompHeaderAccessor stompHeaderAccessor, Message<?> message, MessageHeaders messageHeaders)
    throws JsonProcessingException {
//      TODO: check destination and recipient id
//      TODO: set key to null
//        BadRequestException badRequestException = new BadRequestException();
//        ChatMessageVM chatMessageVM = (ChatMessageVM) compositeMessageConverter.fromMessage(message, ChatMessageVM.class);
//        if (chatMessageVM == null) throw new BadRequestException();
//
//        UUID accountId = Convert.toUUIDOrThrow(stompHeaderAccessor.getFirstNativeHeader(StompHeader.ACCOUNT_ID), badRequestException);
//        UUID identityToken = Convert.toUUIDOrThrow(stompHeaderAccessor.getFirstNativeHeader(StompHeader.IDENTITY_TOKEN), badRequestException);
//        UUID recipientId = Convert.toUUIDOrThrow(stompHeaderAccessor.getFirstNativeHeader(StompHeader.RECIPIENT_ID), badRequestException);
//        validateDestination(messageHeaders.get(StompHeader.SIMP_DESTINATION), recipientId);
//        long receipt = Convert.toLongOrThrow(stompHeaderAccessor.getFirstNativeHeader(StompHeader.RECEIPT), badRequestException);
//        long chatMessageId = chatService.saveChatMessage(accountId,
//                                                         identityToken,
//                                                         recipientId,
//                                                         receipt,
//                                                         chatMessageVM.getBody(),
//                                                         chatMessageVM.getCreatedAt());
//        chatMessageVM.setId(chatMessageId);
//        return MessageBuilder.createMessage(objectMapper.writeValueAsString(chatMessageVM), stompHeaderAccessor.getMessageHeaders());
        return null;
    }

    private void validateDestination(Object destination, UUID id) {
        if (destination == null || !destination.toString().replace("/queue/", "").equals(id.toString()))
            throw new BadRequestException();
    }
}
