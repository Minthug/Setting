package setting.SettingServer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import setting.SettingServer.repository.chat.ChatRoomRepository;
import setting.SettingServer.service.chat.ChatRoomService;
import setting.SettingServer.service.redis.RedisPubSubService;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final RedisPubSubService redisPublisher;
    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
}

