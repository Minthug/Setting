package setting.SettingServer.service.chat;

import setting.SettingServer.dto.chat.ChatDto;
import setting.SettingServer.entity.chat.ChatRoom;

public interface ChatRoomService {

    public ChatDto.CreateChatRoomResponse createChatRoomForPersonal(ChatDto.CreateChatRoomRequest chatRoomRequest);

    public ChatDto.ChatRoomInfoResponse chatRoomInfo(long roomId, int page, int size);

    public ChatRoom findById(long chatRoomId);

    public ChatDto.ChatRoomListResponse getChatRoomList(int page, int size);
}
