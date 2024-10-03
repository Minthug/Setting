package setting.SettingServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import setting.SettingServer.entity.OauthType;
import setting.SettingServer.entity.Member;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByRefreshToken(String refreshToken);

    Optional<Member> findByName(String name);

    Optional<Member> findByOauthTypeAndProvider(OauthType oauthType, String providerId);
}

