package setting.SettingServer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
public class RedisPubSubService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    @Autowired
    public RedisPubSubService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.topic = new ChannelTopic("MyTopic");
    }

    public void publish(String message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }

    public void configureSubscriber() {
        redisTemplate.getConnectionFactory().getConnection().subscribe((message, pattern) -> {
            System.out.println("Received message: " + new String(message.getBody()));
        }, topic.getTopic().getBytes());
    }
}
