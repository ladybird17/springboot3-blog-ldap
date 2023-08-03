//package me.yejin.springboot3blogldap.config;
//
//import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
//
//import jakarta.servlet.Filter;
//import lombok.RequiredArgsConstructor;
//import me.yejin.springboot3blogldap.config.jwt.TokenProvider;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.ldap.core.LdapTemplate;
//import org.springframework.ldap.core.support.LdapContextSource;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
///**
// * author : yjseo
// * <p>
// * date : 2023-06-20
// */
//@EnableWebSecurity
//@RequiredArgsConstructor
//@Configuration
//public class WebSecurityConfig {
//
//  @Bean
//  public WebSecurityCustomizer configure() {
//    //스프링 시큐리티 기능 비활성화
//    return (web) -> web.ignoring()
//        .requestMatchers(toH2Console())
//        .requestMatchers("/static/**");
//  }
//
//  @Bean
//  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//    //특정 HTTP 요청에 대한 웹 기반 보안 구성
//    return http.authorizeHttpRequests(authorize -> authorize
//          .requestMatchers("/login").permitAll()
//          .requestMatchers(HttpMethod.DELETE, "/api/articles/*").hasRole("ADMIN")
//          .anyRequest().authenticated()
//        )//인증, 인가 설정
//        .formLogin(login -> login.permitAll().defaultSuccessUrl("/articles")
//        )//폼 기반 로그인 설정
//        .logout(logout -> logout.logoutSuccessUrl("/login").invalidateHttpSession(true)
//        )//로그아웃 설정
//        .csrf(AbstractHttpConfigurer::disable)//csrf 비활성화
//        .oauth2Login(oauth2 -> oauth2.permitAll().defaultSuccessUrl("/articles"))
//        .build();
//  }
//
//  @Autowired
//  public void configure(AuthenticationManagerBuilder auth) throws Exception {
//
//    auth
//        .ldapAuthentication()
//        .userDnPatterns("uid={0},ou=users")
//        .groupSearchBase("ou=groups")
//        .contextSource()
//        .url("ldap://localhost:389/dc=ldap,dc=yejin,dc=co,dc=kr")
//        .managerDn("cn=admin,dc=ldap,dc=yejin,dc=co,dc=kr")
//        .managerPassword("admin")
//        .and()
//        .passwordCompare()
//        .passwordEncoder(new LdapShaPasswordEncoder())
//        .passwordAttribute("userPassword");
//  }
//
//  @Bean
//  public LdapTemplate ldapTemplate() {
//    return new LdapTemplate(contextSource());
//  }
//
//  @Bean
//  public LdapContextSource contextSource() {
//    LdapContextSource contextSource = new LdapContextSource();
//
//    contextSource.setUrl("ldap://localhost:389");
//    contextSource.setBase("dc=ldap,dc=yejin,dc=co,dc=kr");
//    contextSource.setUserDn("cn=admin,dc=ldap,dc=yejin,dc=co,dc=kr");
//    contextSource.setPassword("admin");
//
//    return contextSource;
//  }
//
//}