package setting.SettingServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import setting.SettingServer.entity.OauthType;
import setting.SettingServer.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByRefreshToken(String refreshToken);

    Optional<User> findByName(String name);

    Optional<User> findByOauthTypeAndProvider(OauthType oauthType, String providerId);
}

