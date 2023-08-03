package me.yejin.springboot3blogldap.repository;

import java.util.Optional;
import me.yejin.springboot3blogldap.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * author : yjseo
 * <p>
 * date : 2023-06-26
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByUsername(String userId);
  Optional<RefreshToken> findByRefreshToken(String refreshToken);
}