package setting.SettingServer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import setting.SettingServer.entity.User;
import setting.SettingServer.entity.UserRole;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginDto {

    private String email;
    private String password;

    public User toUser(PasswordEncoder encoder) {
        return User.builder()
                .email(email)
                .password(encoder.encode(password))
                .role(UserRole.USER)
                .build();
    }

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
