package setting.SettingServer.common.oauth.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import setting.SettingServer.config.jwt.service.JwtService;
import setting.SettingServer.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import setting.SettingServer.user.OAuth2UserUnlinkManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class OauthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final OAuth2UserUnlinkManager oAuth2UserUnlinkManager;
    private final JwtService jwtService;

    

}
