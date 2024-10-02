package setting.SettingServer.common.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import setting.SettingServer.entity.OauthUser;

public interface SocialOauth<T extends OauthToken, U extends OauthUser> {

    /**
     *
     * OAuth 인증을 위한 리다이렉트 URL을 생성합니다.
     * @return 생성된 리다이렉트 URL
     */
    String getOauthRedirectURL(String type);

    /**
     * 인증 코드를 사용하여 액세스 토큰을 요청합니다.
     * @param code 인증 코드
     * @return Oauth 토큰 객체
     */
    T requestAccessToken(String code);

    /**
     * 액세스 토큰 응답을 파싱하여 OAuth 토큰 객체를 반환합니다.
     * @param response 액세스 토큰 응답
     * @return 파싱된 OAuth 토큰 객체
     * @throws JsonProcessingException
     */
    T getAccessToken(ResponseEntity<String> response) throws JsonProcessingException;

    /**
     * OAuth 토큰을 사용하여 사용자 정보를 요청합니다.
     * @param oauthToken OAuth 토큰 객체
     * @return 사용자 정보 응답
     */
    ResponseEntity<String> requestUserInfo(T oauthToken);

    /**
     * 사용자 정보 응답을 파싱하여 사용자 객체를 반환합니다.
     * @param userInfoResponse 사용자 정보 응답
     * @return 파싱된 사용자 객체
     * @throws JsonProcessingException Json 파싱 오류 발생 시
     */
    U getUserInfo(ResponseEntity<String> userInfoResponse) throws JsonProcessingException;


    /**
     * OAuth 프로세스를 실행하고 사용자 정보를 반환합니다.
     * @param code 인증 코드
     * @return 사용자 정보 객체
     */
    default U executeOAuthProcess(String code) {
        try {
            T token = requestAccessToken(code);
            ResponseEntity<String> userInfoResponse = requestUserInfo(token);
            return getUserInfo(userInfoResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("OAuth process failed", e);
        }
    }
}
