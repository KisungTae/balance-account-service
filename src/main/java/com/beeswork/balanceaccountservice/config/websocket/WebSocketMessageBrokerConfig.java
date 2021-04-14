package com.beeswork.balanceaccountservice.config.websocket;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.SneakyThrows;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompBrokerRelayMessageHandler;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfig implements WebSocketMessageBrokerConfigurer {

    private MessageChannel outChannel;

    @Autowired
    private CompositeMessageConverter compositeMessageConverter;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableSimpleBroker("/queue");
//        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app")
              .enableStompBrokerRelay("/queue")
              .setRelayHost("localhost")
              .setRelayPort(61613)
              .setClientLogin("guest")
              .setClientPasscode("guest");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
//        registry.setMessageSizeLimit(1);
//        registry.setSendBufferSizeLimit(1);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat").withSockJS();
//        registry.setErrorHandler(stompErrorHandler());
    }

    @Bean
    public StompChannelInterceptor chatChannelInterceptor() {
        return new StompChannelInterceptor();
    }
//
//    @Bean
//    public StompErrorHandler stompErrorHandler() {
//        return new StompErrorHandler();
//    }


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(chatChannelInterceptor());
//        registration.interceptors(new ExecutorChannelInterceptor() {
//            @SneakyThrows
//            @Override
//            public void afterMessageHandled(@NonNull Message<?> inMessage,
//                                            @NonNull MessageChannel inChannel,
//                                            @NonNull MessageHandler handler,
//                                            Exception ex) {
//                StompHeaderAccessor inAccessor = StompHeaderAccessor.wrap(inMessage);
//                if (StompCommand.SEND.equals(inAccessor.getCommand()) &&
//                    handler instanceof StompBrokerRelayMessageHandler) {
//
//                    String receipt = inAccessor.getReceipt();
//                    ChatMessageDTO chatMessageDTO =
//                            (ChatMessageDTO) compositeMessageConverter.fromMessage(inMessage, ChatMessageDTO.class);
//
//                    if (StringUtils.isEmpty(receipt) || outChannel == null || chatMessageDTO == null) return;
//
//                    chatMessageDTO.setAccountId(null);
//                    chatMessageDTO.setMessage(null);
//                    chatMessageDTO.setRecipientId(null);
//
//                    StompHeaderAccessor outAccessor = StompHeaderAccessor.create(StompCommand.RECEIPT);
//                    outAccessor.setSessionId(inAccessor.getSessionId());
//                    outAccessor.setReceiptId(receipt);
//                    outAccessor.setMessageId(inAccessor.getMessageId());
//                    outChannel.send(MessageBuilder.createMessage(objectMapper.writeValueAsString(chatMessageDTO)
//                                                                             .getBytes(),
//                                                                 outAccessor.getMessageHeaders()));
//                }
//            }
//        });
    }

//    @Override
//    public void configureClientOutboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//            @Override
//            public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
//                outChannel = channel;
//            }
//        });
//    }
}
