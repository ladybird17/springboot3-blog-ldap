package me.yejin.springboot3blogldap.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * author : yjseo
 * <p>
 * date : 2023-06-26
 */
@NoArgsConstructor
@Getter
@Entity
public class RefreshToken {

  @Id
  @Column(name = "id", updatable = false)
  private String id;

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
