package com.beeswork.balanceaccountservice.config.websocket;

import com.beeswork.balanceaccountservice.config.security.JWTTokenProvider;
import com.beeswork.balanceaccountservice.constant.HttpHeader;
import com.beeswork.balanceaccountservice.constant.StompHeader;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.login.AccessTokenExpiredException;
import com.beeswork.balanceaccountservice.exception.match.MatchNotFoundException;
import com.beeswork.balanceaccountservice.exception.match.MatchUnmatchedException;
import com.beeswork.balanceaccountservice.service.account.AccountService;
import com.beeswork.balanceaccountservice.service.chat.ChatService;
import com.beeswork.balanceaccountservice.util.Convert;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.AccessToken;
import io.jsonwebtoken.ExpiredJwtException;
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
import org.springframework.security.core.Authentication;
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

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    private static final String QUEUE = "queue/";

    //  NOTE 1. if exception is thrown from compositeMessageConverter.fromMessage for invalid UUId or Long then ignore the request
    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
        StompCommand stompCommand = stompHeaderAccessor.getCommand();
        String accessToken = stompHeaderAccessor.getFirstNativeHeader(HttpHeader.ACCESS_TOKEN);
        String identityToken = stompHeaderAccessor.getFirstNativeHeader(HttpHeader.IDENTITY_TOKEN);

        if (StompCommand.SUBSCRIBE.equals(stompCommand))
            return validateBeforeSubscribe(stompHeaderAccessor, message, accessToken, identityToken);
        else if (StompCommand.SEND.equals(stompCommand)) {
            throw new BadRequestException();
//            return validateBeforeSend(stompHeaderAccessor, message, accessToken, identityToken);
        }


        return message;
    }

    private void validateAccessToken(String accessToken, String identityToken) {
//        if (!jwtTokenProvider.validateAccessToken(accessToken)) throw new AccessTokenExpiredException();
//        jwtTokenProvider.validateAuthentication(accessToken, identityToken);
    }

    private Message<?> validateBeforeSubscribe(StompHeaderAccessor headerAccessor,
                                               Message<?> message,
                                               String accessToken,
                                               String identityToken) {
//        String destination = QUEUE + jwtTokenProvider.getUserName(accessToken);
//        if (!destination.equals(headerAccessor.getDestination())) throw new BadRequestException();

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


    private Message<?> validateBeforeSend(StompHeaderAccessor stompHeaderAccessor,
                                          Message<?> message,
                                          String accessToken,
                                          String identityToken) throws JsonProcessingException {
        try {
            ChatMessageVM chatMessageVM = (ChatMessageVM) compositeMessageConverter.fromMessage(message, ChatMessageVM.class);
            String receipt = stompHeaderAccessor.getFirstNativeHeader(StompHeader.RECEIPT);
            if (chatMessageVM == null || receipt == null) throw new BadRequestException();
            Long chatMessageId = chatService.saveChatMessage(chatMessageVM.getAccountId(),
                                                             chatMessageVM.getChatId(),
                                                             chatMessageVM.getRecipientId(),
                                                             Long.parseLong(receipt),
                                                             chatMessageVM.getBody(),
                                                             chatMessageVM.getCreatedAt());
            chatMessageVM.setId(chatMessageId);
            return MessageBuilder.createMessage(objectMapper.writeValueAsString(chatMessageVM), stompHeaderAccessor.getMessageHeaders());
        } catch (MatchNotFoundException | MatchUnmatchedException exception) {
            ChatMessageVM chatMessageVM = new ChatMessageVM(false, exception.getExceptionCode());
            chatMessageVM.setError(exception.getExceptionCode());
            return MessageBuilder.createMessage(objectMapper.writeValueAsString(chatMessageVM), stompHeaderAccessor.getMessageHeaders());
        } catch (Exception exception) {
            ChatMessageVM chatMessageVM = new ChatMessageVM(false, null);
            return MessageBuilder.createMessage(objectMapper.writeValueAsString(chatMessageVM), stompHeaderAccessor.getMessageHeaders());
        }
    }
}
