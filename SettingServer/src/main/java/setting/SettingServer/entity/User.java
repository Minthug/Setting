package setting.SettingServer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String email;
    private String name;
    private String password;

    private String accessToken;
    private String refreshToken;

    private String providerId;
    private String provider;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private UserType type;

    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateUserType(UserType type) {
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

    public void updateUser(String name, String encryptedPassword) {
        if (name != null && !name.isEmpty()) this.name = name;
        if (password != null && !password.isEmpty()) this.password = password;
    }
}
