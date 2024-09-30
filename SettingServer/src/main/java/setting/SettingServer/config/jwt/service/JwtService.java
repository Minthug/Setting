package setting.SettingServer.config.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import setting.SettingServer.entity.JwtTokenType;
import setting.SettingServer.repository.UserRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

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

    private final UserRepository userRepository;

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

    private Optional<String> extractEmailFromToken(String accessToken) {
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(accessToken)
                    .getClaim(JwtConstans.EMAIL_CLAIMS)
                    .asString());
        } catch (Exception e) {
            log.error("Failed to extract email from token: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return extractTokenFromHeader(request, accessHeader, true);
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

    class JwtConstans {
        public static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
        public static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
        public static final String EMAIL_CLAIMS = "email";
        public static final String BEARER = "Bearer ";
    }
}
