package me.yejin.springboot3blogldap.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import me.yejin.springboot3blogldap.config.jwt.TokenProvider;
import me.yejin.springboot3blogldap.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import me.yejin.springboot3blogldap.config.oauth.OAuth2SuccessHandler;
import me.yejin.springboot3blogldap.config.oauth.OAuth2UserCustomService;
import me.yejin.springboot3blogldap.repository.RefreshTokenRepository;
import me.yejin.springboot3blogldap.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * author : yjseo
 * <p>
 * date : 2023-06-20
 */
@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

  private final OAuth2UserCustomService oAuth2UserCustomService;
  private final TokenProvider tokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;
  private final UserService userService;
  private final HttpSession httpSession;
  @Bean
  public WebSecurityCustomizer configure() {
    //스프링 시큐리티 기능 비활성화
    return (web) -> web.ignoring()
        .requestMatchers(toH2Console())
        .requestMatchers("/static/**");
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // 헤더를 확인할 커스텀 필터 추가
    http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    //특정 HTTP 요청에 대한 웹 기반 보안 구성
    http.authorizeHttpRequests(authorize -> authorize
      .requestMatchers("/login").permitAll()
      .requestMatchers("/api/token").permitAll()
      .requestMatchers("/api/**").authenticated()
      .requestMatchers(HttpMethod.DELETE, "/api/articles/*").hasRole("ADMIN")
      .anyRequest().authenticated()
    )//인증, 인가 설정
    .formLogin(login -> login.permitAll().defaultSuccessUrl("/articles")
    )//폼 기반 로그인 설정
    .logout(logout -> logout.logoutSuccessUrl("/login").invalidateHttpSession(true)
    )//로그아웃 설정
    .csrf(AbstractHttpConfigurer::disable)//csrf 비활성화
//        .oauth2Login(oauth2 -> oauth2.permitAll().defaultSuccessUrl("/articles"))
    .oauth2Login(login -> login
        //        Authorization 요청과 관련된 상태 저장
        .authorizationEndpoint(endpoint -> endpoint
            .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
        )
        .successHandler(oAuth2SuccessHandler()) //인증 성공시 실행할 핸들러
        .userInfoEndpoint(userInfo -> userInfo
            .userService(oAuth2UserCustomService)
        )
    );

    http.exceptionHandling(config -> config
    .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
    new AntPathRequestMatcher("/api/**")));

    return http.build();
  }

  @Autowired
  public void configure(AuthenticationManagerBuilder auth) throws Exception {

    auth
        .ldapAuthentication()
        .userDnPatterns("uid={0},ou=users")
        .groupSearchBase("ou=groups")
        .contextSource()
        .url("ldap://localhost:389/dc=ldap,dc=yejin,dc=co,dc=kr")
        .managerDn("cn=admin,dc=ldap,dc=yejin,dc=co,dc=kr")
        .managerPassword("admin")
        .and()
        .passwordCompare()
        .passwordEncoder(new LdapShaPasswordEncoder())
        .passwordAttribute("userPassword");
  }

  @Bean
  public LdapTemplate ldapTemplate() {
    return new LdapTemplate(contextSource());
  }

  @Bean
  public LdapContextSource contextSource() {
    LdapContextSource contextSource = new LdapContextSource();

    contextSource.setUrl("ldap://localhost:389");
    contextSource.setBase("dc=ldap,dc=yejin,dc=co,dc=kr");
    contextSource.setUserDn("cn=admin,dc=ldap,dc=yejin,dc=co,dc=kr");
    contextSource.setPassword("admin");

    return contextSource;
  }

  @Bean
  public OAuth2SuccessHandler oAuth2SuccessHandler() {
    return new OAuth2SuccessHandler(tokenProvider,
        refreshTokenRepository,
        oAuth2AuthorizationRequestBasedOnCookieRepository(),
        userService,
        httpSession
    );
  }

  @Bean
  public TokenAuthenticationFilter tokenAuthenticationFilter() {
    return new TokenAuthenticationFilter(tokenProvider);
  }

  @Bean
  public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
    return new OAuth2AuthorizationRequestBasedOnCookieRepository();
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

}