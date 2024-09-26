package setting.SettingServer;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Setter
public class ChatMessage {
    private MessageType type;
    private String content;
    private String sender;
    private String receiver;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        SERVER,
        PRIVATE
    }
}
