package setting.SettingServer.config.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import setting.SettingServer.common.JwtAuthenticationException;
import setting.SettingServer.config.jwt.service.JwtService;
import setting.SettingServer.entity.JwtTokenType;
import setting.SettingServer.entity.User;
import setting.SettingServer.repository.UserRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private static final Set<String> NO_CHECK_URL = new HashSet<>(Arrays.asList(
            "/login", "/signup", "/refresh", "/v1/oauth/authorization/{provider}",
            "/v1/oauth/oauth2/authorization/{provider}", "/v1/oauth/{provider}",
            "/v1/oauth/{type}"));

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isNoCheckUrl(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            processJwtAuthentication(request, response, filterChain);
        } catch (Exception e) {
            handlerFilterException(response, e);
        }
    }


    private void processJwtAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        if (refreshToken != null) {
            processRefreshToken(response, refreshToken);
        } else {
            processAccessToken(request, response, filterChain);
        }
    }

    private void processRefreshToken(HttpServletResponse response, String refreshToken) {
        userRepository.findByRefreshToken(refreshToken)
                .ifPresentOrElse(
                        user -> reissueTokens(response, user),
                        () -> {
                            log.warn("Refresh token is valid but user not found");
                            throw new JwtAuthenticationException("Refresh token is valid but user not found");
                        }
                );
    }

    private void reissueTokens(HttpServletResponse response, User user) {
        String newRefreshToken = reIssueRefreshToken(user);
        jwtService.sendAccessAndRefreshToken(response, user.getEmail(), newRefreshToken);
    }

    private String reIssueRefreshToken(User user) {
        String reIssueRefreshToken = jwtService.createToken(user.getEmail(), JwtTokenType.REFRESH);
        user.updateRefreshToken(reIssueRefreshToken);
        userRepository.saveAndFlush(user);
        return reIssueRefreshToken;
    }

    private void processAccessToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException{
        jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .ifPresent(this::authenticationUser);

        filterChain.doFilter(request, response);
    }

    private void authenticationUser(String accessToken) {
        jwtService.extractEmail(accessToken)
                .flatMap(userRepository::findByEmail)
                .ifPresent(this::saveAuthentication);
    }

    private void saveAuthentication(User user) {
        UserDetails userDetails = createUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private UserDetails createUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password("")
                .authorities(user.getRole().name())
                .build();
    }

    private boolean isNoCheckUrl(String requestURI) {
        return NO_CHECK_URL.contains(requestURI);
    }

    private void handlerFilterException(HttpServletResponse response, Exception e) throws IOException {
        log.error("Jwt Authentication error: {}", e);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }
}
