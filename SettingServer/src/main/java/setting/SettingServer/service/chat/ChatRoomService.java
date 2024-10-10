package setting.SettingServer.service.chat;

import setting.SettingServer.dto.chat.ChatRoomInfoResponse;
import setting.SettingServer.dto.chat.ChatRoomListResponse;
import setting.SettingServer.dto.chat.CreatedChatRoomRequest;
import setting.SettingServer.dto.chat.CreatedChatRoomResponse;
import setting.SettingServer.entity.chat.ChatRoom;

public interface ChatRoomService {

    public CreatedChatRoomResponse createChatRoomForPersonal(CreatedChatRoomRequest chatRoomRequest);

    public ChatRoomInfoResponse chatRoomInfo(long roomId, int page, int size);

    public ChatRoom findById(long chatRoomId);

    public ChatRoomListResponse getChatRoomList(int page, int size);
}
