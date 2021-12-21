package com.beeswork.balanceaccountservice.config.websocket;

import com.beeswork.balanceaccountservice.config.security.JWTTokenProvider;
import com.beeswork.balanceaccountservice.constant.HttpHeader;
import com.beeswork.balanceaccountservice.constant.StompHeader;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.exception.jwt.ExpiredJWTException;
import com.beeswork.balanceaccountservice.service.account.AccountService;
import com.beeswork.balanceaccountservice.service.chat.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.micrometer.core.lang.NonNull;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;

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
    private ModelMapper modelMapper;

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    private static final String QUEUE = "/queue/";

    //  NOTE 1. if exception is thrown from compositeMessageConverter.fromMessage for invalid UUId or Long then ignore the request
    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
        String accessToken = stompHeaderAccessor.getFirstNativeHeader(HttpHeader.ACCESS_TOKEN);


        StompCommand stompCommand = stompHeaderAccessor.getCommand();
        if (stompCommand == null) {
            throw new BadRequestException();
        }

        Jws<Claims> jws = null;
        if (stompCommand == StompCommand.CONNECT || stompCommand == StompCommand.SUBSCRIBE || stompCommand == StompCommand.SEND) {
            jws = jwtTokenProvider.parseJWTToken(accessToken);
            jwtTokenProvider.validateJWTToken(jws);
        }

        if (stompCommand == StompCommand.SUBSCRIBE) {
            String userName = jwtTokenProvider.getUserName(jws);
            String correctDestination = QUEUE + userName;
            if (!correctDestination.equals(stompHeaderAccessor.getDestination())) {
                throw new BadRequestException();
            }
            return updateSubscribeHeaders(stompHeaderAccessor, message);
        } else if (stompCommand == StompCommand.SEND) {
            throw new ExpiredJWTException();
//            ChatMessageVM chatMessageVM = (ChatMessageVM) compositeMessageConverter.fromMessage(message, ChatMessageVM.class);
//            if (chatMessageVM == null) {
//                return message;
//            }
//
//            ChatMessageDTO chatMessageDTO = modelMapper.map(chatMessageVM, ChatMessageDTO.class);
//            String userName = jwtTokenProvider.getUserName(jws);
//            if (!userName.equals(chatMessageDTO.getAccountId().toString())) {
//                throw new AccountNotFoundException();
//            }
//            SaveChatMessageDTO saveChatMessageDTO = chatService.saveChatMessage(chatMessageDTO);
//            if (saveChatMessageDTO.isError()) {
//                chatMessageVM = new ChatMessageVM(saveChatMessageDTO.getError());
//            } else {
//                chatMessageVM.setId(saveChatMessageDTO.getId());
//                chatMessageVM.setCreatedAt(saveChatMessageDTO.getCreatedAt());
//            }
//            return MessageBuilder.createMessage(objectMapper.writeValueAsString(chatMessageVM),
//                                                stompHeaderAccessor.getMessageHeaders());
        }
        return message;
    }

    private Message<?> updateSubscribeHeaders(StompHeaderAccessor stompHeaderAccessor, Message<?> message) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setSessionId(stompHeaderAccessor.getSessionId());
        accessor.setDestination(stompHeaderAccessor.getDestination());
        accessor.addNativeHeader(StompHeader.AUTO_DELETE, String.valueOf(true));
        accessor.addNativeHeader(StompHeader.DURABLE, String.valueOf(true));
        accessor.addNativeHeader(StompHeader.EXCLUSIVE, String.valueOf(false));
        accessor.setSubscriptionId(StompHeader.PRIVATE_QUEUE_SUBSCRIPTION_ID);
        accessor.setAck(StompHeader.DEFAULT_ACK);
        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }
}
