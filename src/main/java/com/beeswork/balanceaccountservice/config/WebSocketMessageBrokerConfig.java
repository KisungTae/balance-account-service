package com.beeswork.balanceaccountservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // config.enableSimpleBroker("/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.enableStompBrokerRelay("/queue")
              .setRelayHost("localhost")
              .setRelayPort(61613)
              .setClientLogin("guest")
              .setClientPasscode("guest");
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat").withSockJS();
//        registry.addEndpoint("/chat").withSockJS();
//        registry.addEndpoint("/chat");
//

        registry.setErrorHandler(stompErrorHandler());



        //        registry.addEndpoint("/chat").withSockJS();
    }

    @Bean
    public StompChannelInterceptor chatChannelInterceptor() {
        return new StompChannelInterceptor();
    }

    @Bean
    public StompErrorHandler stompErrorHandler() { return new StompErrorHandler(); }


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(chatChannelInterceptor());

//        registration.interceptors(new ExecutorChannelInterceptor() {
//
//            @Override
//            public void afterMessageHandled(Message<?> inMessage,
//                                            MessageChannel inChannel, MessageHandler handler, Exception ex) {
//
//
//                StompHeaderAccessor inAccessor = StompHeaderAccessor.wrap(inMessage);
//
//                if (StompCommand.SEND.equals(inAccessor.getCommand())) {
//                    String receipt = inAccessor.getReceipt();
//                    if (StringUtils.isEmpty(receipt)) {
//                        return;
//                    }
//
//                    if (outChannel != null) {
//                        StompHeaderAccessor outAccessor = StompHeaderAccessor.create(StompCommand.RECEIPT);
//                        outAccessor.setSessionId(inAccessor.getSessionId());
//                        outAccessor.setReceiptId(receipt);
//                        outAccessor.setLeaveMutable(true);
//                        outAccessor.setMessageId(inAccessor.getMessageId());
//
//                        Message<byte[]> outMessage =
//                                MessageBuilder.createMessage(new byte[0], outAccessor.getMessageHeaders());
//
//                        outChannel.send(outMessage);
//                    }
//                }
//            }
//        });
    }

//    @Override
//    public void configureClientOutboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new OutboundMessageInterceptor());
//    }
//
//    class OutboundMessageInterceptor implements ChannelInterceptor {
//        public void postSend(Message message,
//                             MessageChannel channel,
//                             boolean sent) {
//
//            outChannel = channel;
//        }
//    }


}
