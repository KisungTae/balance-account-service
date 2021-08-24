package com.beeswork.balanceaccountservice.config.websocket;

import com.beeswork.balanceaccountservice.constant.PushType;
import com.beeswork.balanceaccountservice.constant.StompHeader;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.service.chat.ChatService;
import com.beeswork.balanceaccountservice.service.chat.ChatServiceImpl;
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

    private MessageChannel outChannel;

    private static final int MAX_STOMP_MESSAGE_BODY = 500;

//  TODO: clientlogin and passcode should be in credentials.yaml

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
        registry.setMessageSizeLimit(MAX_STOMP_MESSAGE_BODY);
        registry.setSendBufferSizeLimit(MAX_STOMP_MESSAGE_BODY);
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
        registry.addEndpoint("/websocket");
        registry.setErrorHandler(stompErrorHandler());
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
                    if (StringUtils.isEmpty(receipt) || outChannel == null || chatMessageVM == null) return;

                    StompHeaderAccessor outAccessor = StompHeaderAccessor.create(StompCommand.RECEIPT);
                    outAccessor.setSessionId(inAccessor.getSessionId());
                    outAccessor.setReceiptId(inAccessor.getReceipt());

                    Locale locale = StompHeader.getLocaleFromAcceptLanguageHeader(inAccessor);

                    chatMessageVM.setBody(null);
                    if (chatMessageVM.getId() == null) {
                        chatMessageVM.setCreatedAt(null);
                        chatMessageVM.setBody(messageSource.getMessage(BadRequestException.BAD_REQUEST_EXCEPTION, null, locale));
                    } else if (chatMessageVM.getId() == StompHeader.UNMATCHED_RECEIPT_ID) {
                        chatMessageVM.setCreatedAt(null);
                    }
                    byte[] payload = objectMapper.writeValueAsString(chatMessageVM).getBytes();
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
