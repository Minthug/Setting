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

    @Value("${spring.security.oauth2.client.provider.google.authorization-uri}")
    private String authorizationUri;
    @Value("${spring.security.oauth2.client.registration.{provider}.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.{provider}.redirect-uri}")
    private String redirectUri;
    @Value("${spring.security.oauth2.client.registration.{provider}.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.{provider}.scope}")
    private String scope;
    @Value("${spring.security.oauth2.client.registration.{provider}.token-uri}")
    private String tokenUri;
    @Value("${spring.security.oauth2.client.registration.{provider}.user-info-uri}")
    private String userInfoUri;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;


}


