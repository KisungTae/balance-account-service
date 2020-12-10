package com.beeswork.balanceaccountservice.config;

import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

//        registry.addEndpoint("/chat").withSockJS();
        registry.addEndpoint("/chat");
    }

    @Bean
    public ChatSubscriptionInterceptor chatSubscriptionInterceptor() {
        return new ChatSubscriptionInterceptor();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        registration.interceptors(chatSubscriptionInterceptor());

        registration.interceptors(new ExecutorChannelInterceptor() {
            @Override
            public void afterMessageHandled(Message<?> message, MessageChannel channel, MessageHandler handler, Exception ex) {

                StompHeaderAccessor inAccessor = StompHeaderAccessor.wrap(message);

                if (StompCommand.SEND.equals(inAccessor.getCommand())) {
                    String receipt = inAccessor.getReceipt();
                    if (StringUtils.isEmpty(receipt)) {
                        return;
                    }

                    StompHeaderAccessor outAccessor = StompHeaderAccessor.create(StompCommand.RECEIPT);
                    outAccessor.setSessionId(inAccessor.getSessionId());
                    outAccessor.setReceiptId(receipt);
                    outAccessor.setLeaveMutable(true);

                    Message<byte[]> outMessage =
                            MessageBuilder.createMessage(new byte[0], outAccessor.getMessageHeaders());

                    channel.send(outMessage);
                }
            }
        });
    }


}
