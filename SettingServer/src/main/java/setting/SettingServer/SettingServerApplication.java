package setting.SettingServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SettingServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SettingServerApplication.class, args);
	}

}
