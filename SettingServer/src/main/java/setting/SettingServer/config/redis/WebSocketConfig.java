package setting.SettingServer.config.redis;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메모리 기반의 메시지 브로커가 해당 API를 구독하고 있는 클라이언트에게 메시지를 전달한다.
        registry.enableSimpleBroker("/sub");
        // 클라이언트로부터 메시지를 받을 API의 prefix를 설정한다.
        registry.setApplicationDestinationPrefixes("/pub");
    }
}
