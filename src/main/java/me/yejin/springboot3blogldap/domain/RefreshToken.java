package me.yejin.springboot3blogldap.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * author : yjseo
 * <p>
 * date : 2023-06-26
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false)
  private Long id;

  @Column(name = "username", nullable = false, unique = true)
  private String username;

  @Column(name = "refresh_token", nullable = false)
  private String refreshToken;

  public RefreshToken(String username, String refreshToken) {
    this.username = username;
    this.refreshToken = refreshToken;
  }

  public RefreshToken update(String newRefreshToken){
    this.refreshToken = newRefreshToken;
    return this;
  }
}
