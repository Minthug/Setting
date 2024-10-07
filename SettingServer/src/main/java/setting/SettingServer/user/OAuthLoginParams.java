package setting.SettingServer.user;

import org.springframework.util.MultiValueMap;
import setting.SettingServer.entity.ProviderType;

public interface OAuthLoginParams {
    ProviderType oAuthProvider();
    MultiValueMap<String, String> makeBody();

}
