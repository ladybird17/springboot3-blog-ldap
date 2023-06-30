package me.yejin.springboot3blogldap.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * author : yjseo
 * <p>
 * date : 2023-06-20
 */
@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

  @Bean
  public WebSecurityCustomizer configure() {
    //스프링 시큐리티 기능 비활성화
    return (web) -> web.ignoring()
        .requestMatchers(toH2Console())
        .requestMatchers("/static/**");
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //특정 HTTP 요청에 대한 웹 기반 보안 구성
    return http.authorizeHttpRequests(authorize -> authorize
          .requestMatchers("/login", "/signup", "/user").permitAll()
          .anyRequest().authenticated()
        )//인증, 인가 설정
        .formLogin(login -> login.loginPage("/login").permitAll().defaultSuccessUrl("/articles")
        )//폼 기반 로그인 설정
        .logout(logout -> logout.logoutSuccessUrl("/login").invalidateHttpSession(true)
        )//로그아웃 설정
        .csrf(csrf -> csrf.disable()
        )//csrf 비활성화
        .build();
  }

  @Autowired
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .ldapAuthentication()
        .userDnPatterns("uid={0},ou=people")
        .groupSearchBase("ou=groups")
        .contextSource()
        .url("ldap://localhost:8389/dc=springframework,dc=org")
        .and()
        .passwordCompare()
        .passwordEncoder(new BCryptPasswordEncoder())
        .passwordAttribute("userPassword");
  }

}