package setting.SettingServer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import setting.SettingServer.common.exception.DuplicateEmailException;
import setting.SettingServer.config.jwt.dto.TokenDto;
import setting.SettingServer.config.jwt.service.JwtService;
import setting.SettingServer.common.oauth.RequestOAuthInfoService;
import setting.SettingServer.dto.LoginDto;
import setting.SettingServer.dto.SignUpRequestDto;
import setting.SettingServer.entity.JwtTokenType;
import setting.SettingServer.entity.Member;
import setting.SettingServer.entity.UserRole;
import setting.SettingServer.entity.ProviderType;
import setting.SettingServer.repository.MemberRepository;
import setting.SettingServer.user.OAuth2UserInfo;
import setting.SettingServer.user.OAuthLoginParams;

import java.security.InvalidParameterException;
import java.util.DuplicateFormatFlagsException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final MemberService memberService;
    private final RequestOAuthInfoService oAuthInfoService;

    public TokenDto login(OAuthLoginParams params) {
        OAuth2UserInfo oAuth2UserInfo = oAuthInfoService.request(params);
        String email = findOrCreateMember(oAuth2UserInfo);
        return jwtService.createToken(email, JwtTokenType.ACCESS);
    }

    @Transactional
    public void signUp(SignUpRequestDto signUpRequestDto) throws Exception {
        validateUniqueInfo(signUpRequestDto);
        validateRequiredFields(signUpRequestDto);

        Member member = Member.builder()
                .email(signUpRequestDto.getEmail())
                .password(signUpRequestDto.getPassword())
                .name(signUpRequestDto.getName())
                .role(UserRole.USER)
                .type(ProviderType.LOCAL)
                .build();

        member.hashPassword(encoder);
        memberRepository.save(member);
    }

    private void validateRequiredFields(SignUpRequestDto dto) {
        if (StringUtils.isEmpty(dto.getEmail())) {
            throw new InvalidParameterException("Email is required");
        }

        if (StringUtils.isEmpty(dto.getPassword())) {
            throw new InvalidParameterException("Password is required");
        }

        if (StringUtils.isEmpty(dto.getName())) {
            throw new InvalidParameterException("Name is required");
        }
    }

    private void validateUniqueInfo(SignUpRequestDto dto) {
        if (memberRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email already exists");
        }

        if (memberRepository.findByName(dto.getName()).isPresent()) {
            throw new DuplicateFormatFlagsException("Name already exists");
        }
    }

    @Transactional
    public TokenDto login(LoginDto loginDto) {
        Member member = memberRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (member.getType() == null) {
            member.updateProviderType(ProviderType.LOCAL);
            memberRepository.save(member);
        }

        if (member.getType() == ProviderType.LOCAL) {
            if (!encoder.matches(loginDto.getPassword(), member.getPassword())) {
                throw new IllegalArgumentException("Password not matched");
            }
        }

        String accessToken = jwtService.createToken(member.getEmail(), JwtTokenType.ACCESS);
        String refreshToken = jwtService.createToken(member.getEmail(), JwtTokenType.REFRESH);

//        jwtService.updateStoredToken(user.getEmail(), refreshToken, true); // access token은 바로 사용되므로 저장하지 않음
        jwtService.updateStoredToken(member.getEmail(), accessToken, false);

        return new TokenDto(accessToken, refreshToken);
    }


    @Transactional
    public Member registerOrUpdateUser(String email, String name, String providerId, String provider) {
        ProviderType providerType = getUserTypeFromProvider(provider);

        return memberRepository.findByEmail(email)
                .map(user -> {
                    user.updateName(name);
                    user.updateProviderType(providerType);
                    user.updateOauthInfo(providerId, provider);
                    return user;
                })
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .email(email)
                            .name(name)
                            .providerId(providerId)
                            .provider(provider)
                            .role(UserRole.USER)
                            .type(providerType)
                            .build();
                    return memberRepository.save(newMember);
                });
    }

    private ProviderType getUserTypeFromProvider(String provider) {
        switch (provider.toUpperCase()) {
            case "google":
                return ProviderType.GOOGLE;
            case "kakao":
                return ProviderType.KAKAO;
            case "naver":
                return ProviderType.NAVER;
            case "local":
                return ProviderType.LOCAL;
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }
}
