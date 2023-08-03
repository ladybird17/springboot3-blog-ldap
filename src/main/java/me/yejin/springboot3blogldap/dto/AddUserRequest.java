package me.yejin.springboot3blogldap.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * author : yejin
 * <p>
 * date : 2023-08-03
 */
@Setter
@Getter
public class AddUserRequest {
  private String email;
  private String password;
}