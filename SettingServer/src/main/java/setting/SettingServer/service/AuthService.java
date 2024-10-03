package setting.SettingServer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import setting.SettingServer.common.DuplicateEmailException;
import setting.SettingServer.config.jwt.dto.TokenDto;
import setting.SettingServer.config.jwt.service.JwtService;
import setting.SettingServer.dto.LoginDto;
import setting.SettingServer.dto.SignUpRequestDto;
import setting.SettingServer.entity.JwtTokenType;
import setting.SettingServer.entity.User;
import setting.SettingServer.entity.UserRole;
import setting.SettingServer.entity.OauthType;
import setting.SettingServer.repository.UserRepository;

import java.security.InvalidParameterException;
import java.util.DuplicateFormatFlagsException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final UserService userService;

    @Transactional
    public void signUp(SignUpRequestDto signUpRequestDto) throws Exception {
        validateUniqueInfo(signUpRequestDto);
        validateRequiredFields(signUpRequestDto);

        User user = User.builder()
                .email(signUpRequestDto.getEmail())
                .password(signUpRequestDto.getPassword())
                .name(signUpRequestDto.getName())
                .role(UserRole.USER)
                .type(OauthType.LOCAL)
                .build();

        user.hashPassword(encoder);
        userRepository.save(user);
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
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email already exists");
        }

        if (userRepository.findByName(dto.getName()).isPresent()) {
            throw new DuplicateFormatFlagsException("Name already exists");
        }
    }

    @Transactional
    public TokenDto login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getType() == null) {
            user.updateUserType(OauthType.LOCAL);
            userRepository.save(user);
        }

        if (user.getType() == OauthType.LOCAL) {
            if (!encoder.matches(loginDto.getPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Password not matched");
            }
        }

        String accessToken = jwtService.createToken(user.getEmail(), JwtTokenType.ACCESS);
        String refreshToken = jwtService.createToken(user.getEmail(), JwtTokenType.REFRESH);

//        jwtService.updateStoredToken(user.getEmail(), refreshToken, true); // access token은 바로 사용되므로 저장하지 않음
        jwtService.updateStoredToken(user.getEmail(), accessToken, false);

        return new TokenDto(accessToken, refreshToken);
    }


    @Transactional
    public User registerOrUpdateUser(String email, String name, String providerId, String provider) {
        OauthType oauthType = getUserTypeFromProvider(provider);

        return userRepository.findByEmail(email)
                .map(user -> {
                    user.updateName(name);
                    user.updateUserType(oauthType);
                    user.updateOauthInfo(providerId, provider);
                    return user;
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .name(name)
                            .providerId(providerId)
                            .provider(provider)
                            .role(UserRole.USER)
                            .type(oauthType)
                            .build();
                    return userRepository.save(newUser);
                });
    }

    private OauthType getUserTypeFromProvider(String provider) {
        switch (provider.toUpperCase()) {
            case "google":
                return OauthType.GOOGLE;
            case "kakao":
                return OauthType.KAKAO;
            case "naver":
                return OauthType.NAVER;
            case "local":
                return OauthType.LOCAL;
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }
}
