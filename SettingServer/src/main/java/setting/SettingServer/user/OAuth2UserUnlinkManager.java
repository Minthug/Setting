package setting.SettingServer.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import setting.SettingServer.common.exception.OAuth2AuthenticationProcessingException;
import setting.SettingServer.entity.oauthType;

@RequiredArgsConstructor
@Component
public class OAuth2UserUnlinkManager {

    private final GoogleOAuth2UserUnlink googleOAuth2UserUnlink;
    private final KakaoOAuth2UserUnlink kakaoOAuth2UserUnlink;
    private final NaverOAuth2UserUnlink naverOAuth2UserUnlink;

    public void unlink(oauthType provider, String accessToken) {
        if (oauthType.GOOGLE.equals(provider)) {
            googleOAuth2UserUnlink.unlink(accessToken);
        } else if (oauthType.KAKAO.equals(provider)) {
            kakaoOAuth2UserUnlink.unlink(accessToken);
        } else if (oauthType.NAVER.equals(provider)){
            naverOAuth2UserUnlink.unlink(accessToken);
        } else {
            throw new OAuth2AuthenticationProcessingException(
                    "Unlink with " + provider.getRegistrationId() + " is not supported"
            );
        }
    }
}
