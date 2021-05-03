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
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
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
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
        StompCommand stompCommand = stompHeaderAccessor.getCommand();

        if (StompCommand.SUBSCRIBE.equals(stompCommand))
            return validateBeforeSubscribe(stompHeaderAccessor, message);
        else if (StompCommand.SEND.equals(stompCommand))
            return validateBeforeSend(stompHeaderAccessor, message);

        return message;
    }

    private Message<?> validateBeforeSubscribe(StompHeaderAccessor headerAccessor, Message<?> message) {
        BadRequestException badRequestException = new BadRequestException();
        UUID accountId = Convert.toUUIDOrThrow(getIdFromDestination(headerAccessor.getDestination()), badRequestException);
        UUID identityToken = Convert.toUUIDOrThrow(headerAccessor.getFirstNativeHeader(StompHeader.IDENTITY_TOKEN), badRequestException);
        accountService.validateAccount(accountId, identityToken);

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setSessionId(headerAccessor.getSessionId());
        accessor.setDestination(headerAccessor.getDestination());
        accessor.addNativeHeader(StompHeader.AUTO_DELETE, String.valueOf(true));
        accessor.addNativeHeader(StompHeader.DURABLE, String.valueOf(true));
        accessor.addNativeHeader(StompHeader.EXCLUSIVE, String.valueOf(false));
        accessor.setSubscriptionId(StompHeader.PRIVATE_QUEUE_SUBSCRIPTION_ID);
        accessor.setAck(StompHeader.DEFAULT_ACK);
        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }


    private Message<?> validateBeforeSend(StompHeaderAccessor stompHeaderAccessor, Message<?> message)
    throws JsonProcessingException {
        BadRequestException badRequestException = new BadRequestException();
        ChatMessageVM chatMessageVM = (ChatMessageVM) compositeMessageConverter.fromMessage(message, ChatMessageVM.class);
        if (chatMessageVM == null) throw new BadRequestException();

        UUID accountId = Convert.toUUIDOrThrow(stompHeaderAccessor.getFirstNativeHeader(StompHeader.ACCOUNT_ID), badRequestException);
        UUID identityToken = Convert.toUUIDOrThrow(stompHeaderAccessor.getFirstNativeHeader(StompHeader.IDENTITY_TOKEN), badRequestException);
        long receipt = Convert.toLongOrThrow(stompHeaderAccessor.getFirstNativeHeader(StompHeader.RECEIPT), badRequestException);
        Long chatMessageId = chatService.saveChatMessage(accountId,
                                                         identityToken,
                                                         chatMessageVM.getChatId(),
                                                         chatMessageVM.getRecipientId(),
                                                         receipt,
                                                         chatMessageVM.getBody(),
                                                         chatMessageVM.getCreatedAt());
        chatMessageVM.setId(chatMessageId);
        return MessageBuilder.createMessage(objectMapper.writeValueAsString(chatMessageVM), stompHeaderAccessor.getMessageHeaders());
    }

    private String getIdFromDestination(Object destination) {
        if (destination == null) throw new BadRequestException();
        return destination.toString().replace(StompHeader.QUEUE_PREFIX, "");
    }

}
