package setting.SettingServer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import setting.SettingServer.entity.ChatMessage;

@Service
@RequiredArgsConstructor
public class RedisPubSubService {

    private final RedisTemplate<String, Object> redisTemplate;


    public void publish(ChannelTopic topic, ChatMessage message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
