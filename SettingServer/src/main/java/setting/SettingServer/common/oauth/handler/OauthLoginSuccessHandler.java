package setting.SettingServer.common.oauth.handler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import setting.SettingServer.config.jwt.service.JwtService;
import setting.SettingServer.entity.JwtTokenType;
import setting.SettingServer.entity.UserRole;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OauthLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login Success");

        CustomOauthUser oauthUser = (CustomOauthUser) authentication.getPrincipal();

        if (oauthUser.getRole() == UserRole.GUEST) {
            String accessToken = jwtService.createToken(oauthUser.getEmail(), JwtTokenType.ACCESS);
            String refreshToken = jwtService.createToken(oauthUser.getEmail(), JwtTokenType.REFRESH);
            response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
            jwtService.sendAccessAndRefreshTokenCookie(response, accessToken, null);
            response.sendRedirect("http://localhost:8000");
        } else {
            loginSuccess(response, oauthUser);
        }
    }

    private void loginSuccess(HttpServletResponse response, CustomOauthUser oauthUser) throws IOException {
        String accessToken = jwtService.createToken(oauthUser.getEmail(), JwtTokenType.ACCESS);
        String refreshToken = jwtService.createToken(oauthUser.getEmail(), JwtTokenType.REFRESH);

        jwtService.sendAccessAndRefreshTokenHeader(response, accessToken, refreshToken);
        jwtService.sendAccessAndRefreshTokenCookie(response, accessToken, refreshToken);
        jwtService.updateStoredToken(oauthUser.getEmail(), refreshToken, false);
        response.sendRedirect("http://localhost:8000");
    }
}
