package setting.SettingServer.controller;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import setting.SettingServer.common.oauth.AuthTokens;
import setting.SettingServer.config.jwt.service.LoginService;
import setting.SettingServer.service.AuthService;
import setting.SettingServer.user.params.GoogleLoginParams;
import setting.SettingServer.user.params.KakaoLoginParams;
import setting.SettingServer.user.params.NaverLoginParams;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;


    @PostMapping("/google")
    public ResponseEntity<AuthTokens> loginGoogle(@RequestBody GoogleLoginParams params) {
        return ResponseEntity.ok(loginService.login(params));
    }

    @PostMapping("/kakao")
    public ResponseEntity<AuthTokens> loginKakao(@RequestBody KakaoLoginParams params) {
        return ResponseEntity.ok(loginService.login(params));
    }

    @PostMapping("/naver")
    public ResponseEntity<AuthTokens> loginNaver(@RequestBody NaverLoginParams params) {
        return ResponseEntity.ok(loginService.login(params));
    }

}
