package setting.SettingServer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import setting.SettingServer.config.jwt.service.JwtService;
import setting.SettingServer.repository.MemberRepository;
import setting.SettingServer.service.AuthService;
import setting.SettingServer.service.MemberService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;
    private final JwtService jwtService;
    private final MemberRepository memberRepository;


}
