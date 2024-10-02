package setting.SettingServer.common.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetSocialOauthRes {

    private long userNum;
    private String accessToken;
    private String refreshToken;
    private String email;
    private String name;
}
