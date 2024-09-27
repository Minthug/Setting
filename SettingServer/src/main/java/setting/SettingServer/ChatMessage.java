package setting.SettingServer;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Setter
public class ChatMessage {
    private String roomId;
    private MessageType type;
    private String content;
    private String sender;
    private String receiver;


    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        SERVER,
        ENTER,
        PRIVATE
    }
}
