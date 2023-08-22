package me.yejin.springboot3blogldap.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import me.yejin.springboot3blogldap.config.jwt.TokenProvider;
import me.yejin.springboot3blogldap.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import me.yejin.springboot3blogldap.config.oauth.OAuth2SuccessHandler;
import me.yejin.springboot3blogldap.config.oauth.OAuth2UserCustomService;
import me.yejin.springboot3blogldap.repository.RefreshTokenRepository;
import me.yejin.springboot3blogldap.service.LdapUserService;
import me.yejin.springboot3blogldap.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.client.RestTemplate;

/**
 * author : yjseo
 * <p>
 * date : 2023-06-20
 */
@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

  private final LdapUserService ldapUserService;
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
      .requestMatchers(HttpMethod.DELETE, "/api/articles/*").hasRole("ADMIN")
      .anyRequest().authenticated()
    )
    .formLogin(login -> login
        .permitAll()
        .defaultSuccessUrl("/articles")
        .successHandler(new LdapSuccessHandler(tokenProvider,
        refreshTokenRepository,
        oAuth2AuthorizationRequestBasedOnCookieRepository(),
        ldapUserService,
        httpSession))
    )
    .logout(logout -> logout
        .logoutSuccessUrl("/login")
        .invalidateHttpSession(true)
        .addLogoutHandler(new KeycloakLogoutHandler(restTemplate()))
    )
    .userDetailsService(ldapUserService)
    .csrf(AbstractHttpConfigurer::disable)//csrf 비활성화
    .oauth2Login(login -> login
        //        Authorization 요청과 관련된 상태 저장
        .authorizationEndpoint(endpoint -> endpoint
            .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
        )
        .successHandler(oAuth2SuccessHandler())
        .userInfoEndpoint(userInfo -> userInfo
            .userService(oAuth2UserCustomService)
        )
    );

    http.exceptionHandling(config -> config
    .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
    new AntPathRequestMatcher("/api/**")));

    http.oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()));
    return http.build();
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
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