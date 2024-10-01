package setting.SettingServer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import setting.SettingServer.common.oauth.SocialOauth;
import setting.SettingServer.config.jwt.service.JwtService;
import setting.SettingServer.repository.UserRepository;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
//    private final AuthService authService;
    private final SocialOauth socialOauth;

    public String request(String type) throws IOException {
        String redirectUrl = socialOauth.getOauthRedirectURL(type);
        return redirectUrl;
    }
}
