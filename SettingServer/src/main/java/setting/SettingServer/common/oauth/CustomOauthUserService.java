package setting.SettingServer.common.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import setting.SettingServer.common.oauth.handler.CustomOauthUser;
import setting.SettingServer.entity.User;
import setting.SettingServer.entity.UserType;
import setting.SettingServer.repository.OauthUserRepository;
import setting.SettingServer.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOauthUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final OauthUserRepository oauthUserRepository;

    private static final String GOOGLE = "google";
    private static final String NAVER = "naver";
    private static final String KAKAO = "kakao";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuthUserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        UserType userType = getUserType(registrationId);
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OauthAttributes extractAttributes = OauthAttributes.of(userType, attributes, userNameAttributeName);
        User createUser = getUser(extractAttributes, userType);

        return new CustomOauthUser(
                Collections.singleton(new SimpleGrantedAuthority(createUser.getRole().getKey())),
                attributes,
                extractAttributes.getNameAttributeKey(),
                createUser.getEmail(),
                createUser.getRole()
        );
    }

    private User getUser()

    private UserType getUserType(String registrationId) {
        switch (registrationId.toLowerCase()) {
            case "google":
                return UserType.GOOGLE;
            case "naver":
                return UserType.NAVER;
            case "kakao":
                return UserType.KAKAO;
            default:
                throw new IllegalArgumentException("지원하지 않는 OAuth2 공급자입니다.");
        }
    }
}
