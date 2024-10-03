package setting.SettingServer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String email;
    private String name;
    private String password;

    @Column(name = "image_url")
    private String imageUrl;

    private String accessToken;
    private String refreshToken;

    private String providerId;
    private String provider;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private OauthType type;

    public <T> Member(String subject, String s, List<T> ts) {

    }

    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateMemberType(OauthType type) {
        this.type = type;
    }

    public void hashPassword(PasswordEncoder encoder) {
        this.password = encoder.encode(this.password);
    }

    public void updateOauthInfo(String providerId, String provider) {
        this.providerId = providerId;
        this.provider = provider;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateProfileImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateMember(String name, String encryptedPassword) {
        if (name != null && !name.isEmpty()) this.name = name;
        if (password != null && !password.isEmpty()) this.password = password;
    }
}
