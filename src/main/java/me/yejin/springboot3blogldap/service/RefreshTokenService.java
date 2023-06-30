package me.yejin.springboot3blogldap.service;

import lombok.RequiredArgsConstructor;
import me.yejin.springboot3blogldap.domain.RefreshToken;
import me.yejin.springboot3blogldap.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

/**
 * author : yjseo
 * <p>
 * date : 2023-06-26
 */
@RequiredArgsConstructor
@Service
public class RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;

  public RefreshToken findByRefreshToken(String refreshToken){
    return refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(()->new IllegalArgumentException("Unexpected token"));
  }
}
