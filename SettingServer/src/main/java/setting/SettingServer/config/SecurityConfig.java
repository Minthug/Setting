package setting.SettingServer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import setting.SettingServer.common.oauth.handler.OauthLoginFailureHandler;
import setting.SettingServer.common.oauth.handler.OauthLoginSuccessHandler;
import setting.SettingServer.config.jwt.service.JwtService;
import setting.SettingServer.config.jwt.service.LoginService;
import setting.SettingServer.repository.UserRepository;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final LoginService loginService;
    private final OauthLoginSuccessHandler oauthLoginSuccessHandler;
    private final OauthLoginFailureHandler oauthLoginFailureHandler;

}
