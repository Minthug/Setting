package setting.SettingServer.user;

import setting.SettingServer.entity.oauthType;

import java.util.Map;

public interface OAuth2UserInfo {


    oauthType getProvider();

    String getAccessToken();

    Map<String, Object> getAttributes();

    String getId();

    String getEmail();

    String getName();

    String getFirstName();

    String getLastName();

    String getNickname();

    String getProfileImageUrl();
}

