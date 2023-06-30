package me.yejin.springboot3blogldap.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * author : yjseo
 * <p>
 * date : 2023-06-20
 */
@Getter
@Setter
public class AddUserRequest {
  private String email;
  private String password;
}
