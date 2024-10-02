package setting.SettingServer.common.oauth;

import lombok.Builder;
import lombok.Getter;
import setting.SettingServer.entity.UserType;

import java.util.Map;

@Getter
public class OauthAttributes {

    private String nameAttributeKey;
    private Oauth2UserInfo oauth2UserInfo;

    @Builder
    public OauthAttributes(String nameAttributeKey, Oauth2UserInfo oauth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    public static OauthAttributes of(UserType userType, String userNameAttributeName,
                                     Map<String, Object> attributes) {
        switch (userType) {
            case GOOGLE:
                return ofGoogle(userNameAttributeName, attributes);
            case NAVER:
                return ofNaver(userNameAttributeName, attributes);
            case KAKAO:
                return ofKakao(userNameAttributeName, attributes);
            default:
                throw new IllegalArgumentException("Unsupported provider: " + userType);
        }
    }

    private static OauthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        return OauthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new KakaoOauthUserInfo(attributes))
                .build();
    }

    private static OauthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        return null;
    }

    private static OauthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OauthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new GoogleOauthUserInfo(attributes))
                .build();
    }
}
