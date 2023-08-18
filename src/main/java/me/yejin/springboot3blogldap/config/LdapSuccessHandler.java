package me.yejin.springboot3blogldap.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import me.yejin.springboot3blogldap.config.jwt.TokenProvider;
import me.yejin.springboot3blogldap.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import me.yejin.springboot3blogldap.domain.LdapUser;
import me.yejin.springboot3blogldap.domain.RefreshToken;
import me.yejin.springboot3blogldap.dto.SessionUser;
import me.yejin.springboot3blogldap.repository.RefreshTokenRepository;
import me.yejin.springboot3blogldap.service.LdapUserService;
import me.yejin.springboot3blogldap.util.CookieUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * author : yejin
 * <p>
 * date : 2023-08-16
 */
@RequiredArgsConstructor
@Component
public class LdapSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
  public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
  public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
  public static final String REDIRECT_PATH = "/articles";

  private final TokenProvider tokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;
  private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
  private final LdapUserService ldapUserService;
  private final HttpSession httpSession;
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
    LdapUser user = ldapUserService.findByEmail(sessionUser.getEmail());

    String refreshToken = tokenProvider.generateLdapToken(user, REFRESH_TOKEN_DURATION);
    saveRefreshToken(user.getUid(), refreshToken);
    addRefreshTokenToCookie(request, response, refreshToken);

    String accessToken = tokenProvider.generateLdapToken(user, ACCESS_TOKEN_DURATION);
    String targetUrl = getTargetUrl(accessToken);

    clearAuthenticationAttributes(request, response);

    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }

  private void saveRefreshToken(String userId, String newRefreshToken) {
    RefreshToken refreshToken = refreshTokenRepository.findByUsername(userId)
        .map(entity -> entity.update(newRefreshToken))
        .orElse(new RefreshToken(userId, newRefreshToken));

    refreshTokenRepository.save(refreshToken);
  }

  private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
    int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();

    CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
    CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
  }

  private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
    super.clearAuthenticationAttributes(request);
    authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
  }

  private String getTargetUrl(String token) {
    return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
        .queryParam("token", token)
        .build()
        .toUriString();
  }
}
