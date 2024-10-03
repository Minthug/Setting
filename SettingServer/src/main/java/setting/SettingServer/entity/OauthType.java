package setting.SettingServer.entity;

import lombok.Getter;

public enum OauthType {

    GOOGLE("google"),
    KAKAO("kakao"),
    NAVER("naver"),
    LOCAL("local");

    @Getter
    private final String text;
    OauthType(String text) {
        this.text = text;
    }
}
