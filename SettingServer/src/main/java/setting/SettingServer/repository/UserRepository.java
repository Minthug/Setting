package setting.SettingServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import setting.SettingServer.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
