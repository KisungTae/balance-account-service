package com.beeswork.balanceaccountservice.config.websocket;

import com.beeswork.balanceaccountservice.config.properties.StompProperties;
import com.beeswork.balanceaccountservice.constant.StompHeader;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.config.annotation.*;

import java.util.Locale;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private CompositeMessageConverter compositeMessageConverter;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private StompProperties stompProperties;

    private MessageChannel outChannel;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes(stompProperties.getDestinationPrefix())
              .enableStompBrokerRelay(stompProperties.getStompBrokerRelay())
              .setRelayHost(stompProperties.getRelayHost())
              .setRelayPort(stompProperties.getRelayPort())
              .setClientLogin(stompProperties.getClientLogin())
              .setClientPasscode(stompProperties.getClientPasscode());
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(stompProperties.getMaxMessageSize());
        registry.setSendBufferSizeLimit(stompProperties.getMaxMessageSize());
    }

    @Bean
    public StompInboundChannelInterceptor stompInboundChannelInterceptor() {
        return new StompInboundChannelInterceptor();
    }

    @Bean
    public StompErrorHandler stompErrorHandler() {
        return new StompErrorHandler();
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(stompProperties.getEndPoint());
        registry.setErrorHandler(stompErrorHandler());
//        registry.addEndpoint(stompProperties.getEndPoint()).setAllowedOrigins("*").addInterceptors(new HandshakeInterceptor() {
//
//            @Override
//            public boolean beforeHandshake(ServerHttpRequest serverHttpRequest,
//                                           ServerHttpResponse serverHttpResponse,
//                                           WebSocketHandler webSocketHandler,
//                                           Map<String, Object> map) throws Exception {
//                System.out.println("public boolean beforeHandshake");
//                return false;
//            }
//
//            @Override
//            public void afterHandshake(ServerHttpRequest serverHttpRequest,
//                                       ServerHttpResponse serverHttpResponse,
//                                       WebSocketHandler webSocketHandler,
//                                       Exception e) {
//
//            }
//        });
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompInboundChannelInterceptor());
        registration.interceptors(new ExecutorChannelInterceptor() {
            @SneakyThrows
            @Override
            public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
                StompHeaderAccessor inAccessor = StompHeaderAccessor.wrap(message);
                if (StompCommand.SEND.equals(inAccessor.getCommand())) {
                    ChatMessageVM chatMessageVM = (ChatMessageVM) compositeMessageConverter.fromMessage(message, ChatMessageVM.class);
                    String receipt = inAccessor.getReceipt();
                    if (StringUtils.isEmpty(receipt) || outChannel == null || chatMessageVM == null) {
                        return;
                    }


                    if (chatMessageVM.isError()) {
                        Locale locale = StompHeader.getLocale(inAccessor);
                        String exceptionMessage = messageSource.getMessage(chatMessageVM.getError(), null, locale);
                        chatMessageVM.setBody(exceptionMessage);
                    } else {
                        chatMessageVM.setBody(null);
                    }

//                    outAccessor.setReceiptId(inAccessor.getReceipt());
                    chatMessageVM.setChatId(null);
                    chatMessageVM.setAccountId(null);
                    chatMessageVM.setRecipientId(null);
                    byte[] payload = objectMapper.writeValueAsString(chatMessageVM).getBytes();

                    StompHeaderAccessor outAccessor = StompHeaderAccessor.create(StompCommand.RECEIPT);
                    outAccessor.setSessionId(inAccessor.getSessionId());

                    outChannel.send(MessageBuilder.createMessage(payload, outAccessor.getMessageHeaders()));
                }
            }
        });
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
                outChannel = channel;
            }
        });
    }
}
