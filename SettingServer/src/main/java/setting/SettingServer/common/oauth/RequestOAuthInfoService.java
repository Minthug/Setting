package setting.SettingServer.common.oauth;

import org.springframework.stereotype.Component;
import setting.SettingServer.entity.ProviderType;
import setting.SettingServer.user.OAuth2UserInfo;
import setting.SettingServer.user.OAuthApiClient;
import setting.SettingServer.user.OAuthLoginParams;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RequestOAuthInfoService {
    private final Map<ProviderType, OAuthApiClient> clients;

    public RequestOAuthInfoService(List<OAuthApiClient> clients) {
        this.clients = clients.stream().collect(
                Collectors.toUnmodifiableMap(OAuthApiClient::oAuthProvider, Function.identity())
        );
    }

    public OAuth2UserInfo request(OAuthLoginParams params) {
        OAuthApiClient client = clients.get(params.oAuthProvider());
        String accessToken = client.requestAccessToken(params);
        return client.requestOauthInfo(accessToken);
    }
}
