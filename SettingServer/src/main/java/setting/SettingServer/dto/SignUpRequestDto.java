package setting.SettingServer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequestDto {

    private String email;
    private String password;
    private String name;

}
