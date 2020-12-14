package com.beeswork.balanceaccountservice.config;

import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfig implements WebSocketMessageBrokerConfigurer {

    private MessageChannel outChannel;


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

//        registry.addEndpoint("/chat").withSockJS();
        registry.addEndpoint("/chat");

        registry.setErrorHandler(new StompSubProtocolErrorHandler() {
            @Override
            protected Message<byte[]> handleInternal(StompHeaderAccessor errorHeaderAccessor,
                                                     byte[] errorPayload,
                                                     Throwable cause,
                                                     StompHeaderAccessor clientHeaderAccessor) {

                errorHeaderAccessor.setMessageId(clientHeaderAccessor.getMessageId());
                return super.handleInternal(errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);
            }
        });
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
            public void afterMessageHandled(Message<?> inMessage,
                                            MessageChannel inChannel, MessageHandler handler, Exception ex) {


                StompHeaderAccessor inAccessor = StompHeaderAccessor.wrap(inMessage);

                if (StompCommand.SEND.equals(inAccessor.getCommand())) {
                    String receipt = inAccessor.getReceipt();
                    if (StringUtils.isEmpty(receipt)) {
                        return;
                    }

                    if (outChannel != null) {
                        StompHeaderAccessor outAccessor = StompHeaderAccessor.create(StompCommand.RECEIPT);
                        outAccessor.setSessionId(inAccessor.getSessionId());
                        outAccessor.setReceiptId(receipt);
                        outAccessor.setLeaveMutable(true);
                        outAccessor.setMessageId(inAccessor.getMessageId());

                        Message<byte[]> outMessage =
                                MessageBuilder.createMessage(new byte[0], outAccessor.getMessageHeaders());

                        outChannel.send(outMessage);
                    }
                }
            }
        });
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(new OutboundMessageInterceptor());
    }

    class OutboundMessageInterceptor implements ChannelInterceptor {
        public void postSend(Message message,
                             MessageChannel channel,
                             boolean sent) {

            outChannel = channel;
        }
    }


}
