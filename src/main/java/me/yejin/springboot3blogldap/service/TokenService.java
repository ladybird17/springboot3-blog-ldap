package me.yejin.springboot3blogldap.service;

import jakarta.servlet.http.HttpSession;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import me.yejin.springboot3blogldap.config.jwt.TokenProvider;
import me.yejin.springboot3blogldap.domain.LdapUser;
import me.yejin.springboot3blogldap.domain.User;
import me.yejin.springboot3blogldap.dto.SessionUser;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

/**
 * author : yjseo
 * <p>
 * date : 2023-07-03
 */
@RequiredArgsConstructor
@Service
public class TokenService {
  private final TokenProvider tokenProvider;
  private final RefreshTokenService refreshTokenService;

  private final UserService userService;
  private final LdapUserService ldapUserService;
  private final HttpSession httpSession;

  public String createNewAccessToken(String refreshToken) {
    // 토큰 유효성 검사에 실패하면 예외 발생
    if(!tokenProvider.validToken(refreshToken)) {
      throw new IllegalArgumentException("Unexpected token");
    }

    Long userId = NumberUtils.toLong(refreshTokenService.findByRefreshToken(refreshToken).getUsername());
    User user = userService.findById(userId);

    return tokenProvider.generateToken(user, Duration.ofHours(2));
  }

  public String createNewLdapAccessToken(String refreshToken){
    if(!tokenProvider.validToken(refreshToken)){
      throw new IllegalArgumentException("Unexpected token");
    }

    String username = refreshTokenService.findByRefreshToken(refreshToken).getUsername();
    LdapUser user = ldapUserService.findLdapUser(username);
    httpSession.setAttribute("user", new SessionUser(user));
    return tokenProvider.generateLdapToken(user, Duration.ofHours(2));
  }
}
