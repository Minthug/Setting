package setting.SettingServer.user;

import setting.SettingServer.common.exception.OAuth2AuthenticationProcessingException;
import setting.SettingServer.entity.oauthType;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId,
                                                   String accessToken,
                                                   Map<String, Object> attributes) {
        if (oauthType.GOOGLE.getRegistrationId().equals(registrationId)) {
            return new GoogleOAuth2UserInfo(attributes, accessToken);
        } else if (oauthType.KAKAO.getRegistrationId().equals(registrationId)) {
            return new KakaoOAuth2UserInfo(attributes, accessToken);
        } else if (oauthType.NAVER.getRegistrationId().equals(registrationId)) {
            return new NaverOAuth2UserInfo(attributes, accessToken);
        } else {
            throw new OAuth2AuthenticationProcessingException("Login with " + registrationId + " is not supported");
        }
    }
}
