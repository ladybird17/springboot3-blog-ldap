package me.yejin.springboot3blogldap.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import me.yejin.springboot3blogldap.config.jwt.TokenProvider;
import me.yejin.springboot3blogldap.domain.LdapUser;
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

  private final LdapUserService userService;

  public String createNewAccessToken(String refreshToken){
    if(!tokenProvider.validToken(refreshToken)){
      throw new IllegalArgumentException("Unexpected token");
    }

    String username = refreshTokenService.findByRefreshToken(refreshToken).getUsername();
    LdapUser user = userService.findLdapUser(username);
    return tokenProvider.generateToken(user, Duration.ofHours(2));
  }
}
