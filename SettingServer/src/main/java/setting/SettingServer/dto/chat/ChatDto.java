package setting.SettingServer.dto.chat;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import setting.SettingServer.entity.Member;
import setting.SettingServer.entity.chat.ChatMessage;
import setting.SettingServer.entity.chat.ChatRoom;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class ChatDto {

    @Getter
    public static class CreateChatRoomRequest {
        private String roomMakerId;
        private String guestId;

    }

    @Getter
    public static class CreateChatRoomResponse {
        private String roomMakerId;
        private String guestId;
        private String chatRoomId;

        public CreateChatRoomResponse(String roomMakerId, String guestId, String chatRoomId) {
            this.roomMakerId = roomMakerId;
            this.guestId = guestId;
            this.chatRoomId = chatRoomId;
        }
    }

    @Getter
    public static class ChatRoomInfoRequest {
        private String roomId;
    }

    @Data
    public static class ChatRoomInfoResponse {
        private String chatRoomId;
        private ChatMessage lastChatMesg;
        private Set<ChatUserInfoDto> chatRoomMembers;
        private List<ChatMessageInfo> latestChatMessages;
        private LocalDateTime localDateTime;

        public ChatRoomInfoResponse(ChatRoom chatRoom) {
            this.chatRoomId = chatRoom.getId();
            this.lastChatMesg = chatRoom.getLatestChatMessage();
            this.chatRoomMembers = new HashSet<>();
                for (Member member : chatRoom.getChatRoomMembers()) {
                    chatRoomMembers.add(new ChatUserInfoDto(member));
                }
            this.localDateTime = chatRoom.getCreatedAt();
        }
    }

    @Data
    @Builder
    public static class ChatRoomListResponse {
        private int page;
        private int count;
        private String reqUserId;
        private List<ChatRoomList> chatRooms;
    }

    @Data
    public static class ChatRoomList {
        private String chatRoomId;
        private ChatMessage lastChatMsg;
        private String guestId;

        public ChatRoomList(ChatRoom chatRoom, long userId) {
            this.chatRoomId = chatRoom.getId();
            this.lastChatMsg = chatRoom.getLatestChatMessage();
            for (Member member : chatRoom.getChatRoomMembers()) {
                if (member.getId() != userId) {
                    this.guestId = String.valueOf(member.getId());
                }
            }
        }
    }
}
