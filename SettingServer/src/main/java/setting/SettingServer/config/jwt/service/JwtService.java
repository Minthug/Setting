package setting.SettingServer.config.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import setting.SettingServer.config.jwt.dto.TokenDto;
import setting.SettingServer.entity.JwtTokenType;
import setting.SettingServer.repository.UserRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtService {
    // Constants
    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    // Configuration properties
    @Value("${spring.security.jwt.secret}")
    private String secret;

    @Value("${spring.security.jwt.accessExpiration}")
    private long accessExpiration;

    @Value("${spring.security.jwt.refreshExpiration}")
    private long refreshExpiration;

    @Value("${spring.security.jwt.header}")
    private String accessHeader;

    @Value("${spring.security.jwt.refreshHeader}")
    private String refreshHeader;

    // Dependencies
    private final UserRepository userRepository;

    // Token Creation
    public String createToken(String email, JwtTokenType type) {
        Date now = new Date();
        Date expiration = calculateExpirationDate(now, type);
        String subject = type == JwtTokenType.ACCESS ? JwtConstans.ACCESS_TOKEN_SUBJECT : JwtConstans.REFRESH_TOKEN_SUBJECT;

        return JWT.create()
                .withSubject(subject)
                .withExpiresAt(expiration)
                .withClaim(JwtConstans.EMAIL_CLAIMS, email)
                .sign(Algorithm.HMAC256(secret));
    }

    private Date calculateExpirationDate(Date now, JwtTokenType type) {
        long expirationPeriod = type == JwtTokenType.ACCESS ? accessExpiration : refreshExpiration;
        return new Date(now.getTime() + expirationPeriod);
    }

    // Token Validation
    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(token);
            return true;
        } catch (Exception e) {
            log.error("Token is invalid: {}", e.getMessage());
            return false;
        }
    }

    // Token Extraction
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return extractTokenFromHeader(request, accessHeader, true);
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        log.info("Attempting to extract Refresh Token");

        return Stream.of(
                        extractTokenFromHeader(request, refreshHeader, true),
                        extractTokenFromHeader(request, refreshHeader, false),
                        extractTokenFromCookie(request, REFRESH_TOKEN_COOKIE_NAME),
                        Optional.ofNullable(request.getParameter(REFRESH_TOKEN_COOKIE_NAME))
                )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    public Optional<String> extractEmail(String accessToken) {
        return extractEmailFromToken(accessToken);
    }

    private Optional<String> extractEmailFromToken(String token) {
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(token)
                    .getClaim(JwtConstans.EMAIL_CLAIMS)
                    .asString());
        } catch (Exception e) {
            log.error("Failed to extract email from token: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<String> extractTokenFromHeader(HttpServletRequest request, String headerName, boolean withBearer) {
        return Optional.ofNullable(request.getHeader(headerName))
                .filter(token -> !withBearer || token.startsWith(JwtConstans.BEARER))
                .map(token -> withBearer ? token.replace(JwtConstans.BEARER, "") : token);
    }

    private Optional<String> extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> cookieName.equals(cookie.getName()))
                        .findFirst()
                        .map(Cookie::getValue));
    }

    // Token Sending
    public void sendTokens(HttpServletResponse response, String accessToken, String refreshToken, boolean useCookie) {
        response.setStatus(HttpServletResponse.SC_OK);
        if (useCookie) {
            sendTokenCookie(response, ACCESS_TOKEN_COOKIE_NAME, accessToken, accessExpiration);
            sendTokenCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, refreshExpiration);
        } else {
            sendTokenHeader(response, accessHeader, accessToken);
            sendTokenHeader(response, refreshHeader, refreshToken);
        }
        log.info("Tokens sent successfully");
    }

    public void sendAccessAndRefreshToken(HttpServletResponse response, String email, String newRefreshToken) {
        String newAccessToken = createToken(email, JwtTokenType.ACCESS);
        updateStoredToken(email, newAccessToken, false);
        sendTokens(response, newAccessToken, newRefreshToken, true);
    }

    public void sendAccessAndRefreshTokenCookie(HttpServletResponse response, String accessToken, String refreshToken) {
        sendTokens(response, accessToken, refreshToken, true);
    }

    public void sendAccessAndRefreshTokenHeader(HttpServletResponse response, String accessToken, String refreshToken) {
        sendTokens(response, accessToken, refreshToken, false);
    }

    private void sendTokenHeader(HttpServletResponse response, String header, String token) {
        response.setHeader(header, token);
    }

    private void sendTokenCookie(HttpServletResponse response, String name, String token, long expiration) {
        CookieUtil.addCookie(response, name, token, expiration / 1000, true);
    }

    // Token Storage
    public void updateStoredToken(String email, String token, boolean isAccessToken) {
        userRepository.findByEmail(email)
                .ifPresentOrElse(user -> {
                            if (isAccessToken) user.updateAccessToken(token);
                            else user.updateRefreshToken(token);
                        },
                        () -> log.error("User not found: {}", email));
    }

    // Token Reissue
    public TokenDto reissueToken(String refreshToken) {
        if(!isTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token is invalid");
        }

        String email = extractEmailFromToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Failed to extract email from token"));

        String newAccessToken = createToken(email, JwtTokenType.ACCESS);
        String newRefreshToken = createToken(email, JwtTokenType.REFRESH);

        updateStoredToken(email, newAccessToken, false);
        return new TokenDto(newAccessToken, newRefreshToken);
    }

    // Inner classes and utilities
    static class JwtConstans {
        public static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
        public static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
        public static final String EMAIL_CLAIMS = "email";
        public static final String BEARER = "Bearer ";
    }

    private static class CookieUtil {
        public static void addCookie(HttpServletResponse response, String name, String value, long maxAge, boolean httpOnly) {
            Cookie cookie = new Cookie(name, value);
            cookie.setPath("/");
            cookie.setSecure(true);
            cookie.setMaxAge(7 * 24 * 60 * 60); // 7 Days
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }
    }
}

