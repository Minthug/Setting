package setting.SettingServer.common.oauth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import setting.SettingServer.config.jwt.service.JwtService;
import setting.SettingServer.entity.JwtTokenType;
import setting.SettingServer.entity.Member;
import setting.SettingServer.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import setting.SettingServer.service.OauthService;
import setting.SettingServer.user.GoogleOAuth2UserInfo;
import setting.SettingServer.user.OAuth2UserInfo;
import setting.SettingServer.user.OAuth2UserInfoFactory;
import setting.SettingServer.user.OAuth2UserUnlinkManager;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OauthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService clientService;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final OAuth2UserUnlinkManager oAuth2UserUnlinkManager;
    private final JwtService jwtService;
    private final OauthService oauthService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;

        String registrationId = authToken.getAuthorizedClientRegistrationId();
        String accessToken = getAccessToken(authToken);

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId,
                accessToken,
                oAuth2User.getAttributes()
        );

        Member member = oauthService.processOAuthPostLogin(userInfo);
        String jwtAccessToken = jwtService.createToken(member.getEmail(), JwtTokenType.ACCESS);
        String refreshToken = jwtService.createToken(member.getEmail(), JwtTokenType.REFRESH);

        response.addHeader("Authorization", "Bearer " + jwtAccessToken);
        response.addHeader("Refresh-Token", refreshToken);

        getRedirectStrategy().sendRedirect(request, response, "/oauth2-success");
    }

    private String getAccessToken(OAuth2AuthenticationToken authToken) {
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                authToken.getAuthorizedClientRegistrationId(),
                authToken.getName());

        OAuth2AccessToken accessToken = client.getAccessToken();
        return accessToken.getTokenValue();
    }
}
