package setting.SettingServer.service;

import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GcpStorageService {

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

}
