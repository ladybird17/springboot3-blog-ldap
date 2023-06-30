package me.yejin.springboot3blogldap.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * author : yjseo
 * <p>
 * date : 2023-06-26
 */
@Getter
@Setter
public class CreateAccessTokenRequest {
  private String refreshToken;
}
