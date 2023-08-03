package me.yejin.springboot3blogldap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * author : yjseo
 * <p>
 * date : 2023-06-20
 */
@Controller
public class UserViewController {

  @GetMapping("/login")
  public String login(){
    return "oauthLogin";
  }

}
