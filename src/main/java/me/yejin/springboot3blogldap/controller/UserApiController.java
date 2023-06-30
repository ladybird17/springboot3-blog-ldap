package me.yejin.springboot3blogldap.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * author : yjseo
 * <p>
 * date : 2023-06-20
 */
@RequiredArgsConstructor
@Controller
public class UserApiController {

  @GetMapping("/logout")
  public String logout(HttpServletRequest request, HttpServletResponse response){
    new SecurityContextLogoutHandler().logout(request, response,
        SecurityContextHolder.getContext().getAuthentication());
    return "redirect:/login";
  }
}
