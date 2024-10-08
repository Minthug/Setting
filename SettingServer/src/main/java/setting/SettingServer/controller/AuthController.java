package setting.SettingServer.controller;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import setting.SettingServer.common.exception.DuplicateEmailException;
import setting.SettingServer.common.oauth.AuthTokens;
import setting.SettingServer.config.jwt.service.LoginService;
import setting.SettingServer.dto.SignUpRequestDto;
import setting.SettingServer.service.AuthService;
import setting.SettingServer.user.params.GoogleLoginParams;
import setting.SettingServer.user.params.KakaoLoginParams;
import setting.SettingServer.user.params.NaverLoginParams;

import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity sign_up(@RequestBody SignUpRequestDto signUpRequestDto) throws Exception {
        if (signUpRequestDto.getEmail() == null || signUpRequestDto.getEmail().trim().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "이메일 주소 입력"));
        }
        if (signUpRequestDto.getPassword() == null || signUpRequestDto.getPassword().trim().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "비밀번호 입력"));
        }
        if (signUpRequestDto.getName() == null || signUpRequestDto.getName().trim().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "이름 입력"));
        }

        try {
            authService.signUp(signUpRequestDto);
            return ResponseEntity.ok().body(Map.of("Message", "회원가입 완료"));
        } catch (DuplicateEmailException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "중복된 이메일"));
        }
    }

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
