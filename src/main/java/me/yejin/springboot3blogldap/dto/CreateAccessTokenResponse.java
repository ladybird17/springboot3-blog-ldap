package me.yejin.springboot3blogldap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * author : yjseo
 * <p>
 * date : 2023-06-26
 */
@AllArgsConstructor
@Getter
public class CreateAccessTokenResponse {
  private String accessToken;
}
