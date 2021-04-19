package com.beeswork.balanceaccountservice.config.websocket;

import com.beeswork.balanceaccountservice.constant.StompHeader;
import com.beeswork.balanceaccountservice.dto.push.ChatMessagePush;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.service.account.AccountService;
import com.beeswork.balanceaccountservice.service.chat.ChatService;
import com.beeswork.balanceaccountservice.util.Convert;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.StringUtils;
import io.micrometer.core.lang.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.simp.stomp.StompBrokerRelayMessageHandler;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;

import java.util.UUID;

public class StompInboundChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private ChatService chatService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CompositeMessageConverter compositeMessageConverter;

    @Autowired
    private ObjectMapper objectMapper;

    private MessageChannel outChannel;


    //  NOTE 1. if exception is thrown from compositeMessageConverter.fromMessage for invalid UUId or Long then ignore the request
    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, @NonNull MessageChannel channel) {
        MessageHeaders messageHeaders = message.getHeaders();
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
        StompCommand stompCommand = stompHeaderAccessor.getCommand();

        if (StompCommand.SUBSCRIBE.equals(stompCommand)) validateBeforeSubscribe(stompHeaderAccessor, messageHeaders);
        else if (StompCommand.SEND.equals(stompCommand)) return validateBeforeSend(stompHeaderAccessor, message);
        return message;
    }

    private void validateBeforeSubscribe(StompHeaderAccessor stompHeaderAccessor, MessageHeaders messageHeaders) {
        BadRequestException badRequestException = new BadRequestException();
        UUID accountId = Convert.toUUIDOrThrow(getIdFromDestination(messageHeaders.get(StompHeader.SIMP_DESTINATION)), badRequestException);
        UUID identityToken = Convert.toUUIDOrThrow(stompHeaderAccessor.getFirstNativeHeader(StompHeader.IDENTITY_TOKEN), badRequestException);
        accountService.validateAccount(accountId, identityToken);

        boolean autoDelete = Convert.toBooleanOrThrow(stompHeaderAccessor.getFirstNativeHeader(StompHeader.AUTO_DELETE), badRequestException);
        boolean exclusive = Convert.toBooleanOrThrow(stompHeaderAccessor.getFirstNativeHeader(StompHeader.EXCLUSIVE), badRequestException);
        boolean durable = Convert.toBooleanOrThrow(stompHeaderAccessor.getFirstNativeHeader(StompHeader.DURABLE), badRequestException);
        if (!autoDelete || exclusive || !durable) throw badRequestException;
    }


    private Message<?> validateBeforeSend(StompHeaderAccessor stompHeaderAccessor, Message<?> message)
    throws JsonProcessingException {
        BadRequestException badRequestException = new BadRequestException();
        ChatMessageVM chatMessageVM = (ChatMessageVM) compositeMessageConverter.fromMessage(message, ChatMessageVM.class);
        if (chatMessageVM == null) throw new BadRequestException();

        UUID accountId = Convert.toUUIDOrThrow(stompHeaderAccessor.getFirstNativeHeader(StompHeader.ACCOUNT_ID), badRequestException);
        UUID identityToken = Convert.toUUIDOrThrow(stompHeaderAccessor.getFirstNativeHeader(StompHeader.IDENTITY_TOKEN), badRequestException);
        UUID recipientId = Convert.toUUIDOrThrow(stompHeaderAccessor.getFirstNativeHeader(StompHeader.RECIPIENT_ID), badRequestException);
        long receipt = Convert.toLongOrThrow(stompHeaderAccessor.getFirstNativeHeader(StompHeader.RECEIPT), badRequestException);
        long chatMessageId = chatService.saveChatMessage(accountId,
                                                         identityToken,
                                                         chatMessageVM.getChatId(),
                                                         recipientId,
                                                         receipt,
                                                         chatMessageVM.getBody(),
                                                         chatMessageVM.getCreatedAt());
        chatMessageVM.setId(chatMessageId);
        return MessageBuilder.createMessage(objectMapper.writeValueAsString(chatMessageVM), stompHeaderAccessor.getMessageHeaders());
    }

    private String getIdFromDestination(Object destination) {
        if (destination == null) throw new BadRequestException();
        return destination.toString().replace("/queue/", "");
    }

}
