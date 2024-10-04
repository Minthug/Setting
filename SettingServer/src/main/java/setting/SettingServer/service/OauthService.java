package setting.SettingServer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import setting.SettingServer.entity.OauthUser;

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;


}


