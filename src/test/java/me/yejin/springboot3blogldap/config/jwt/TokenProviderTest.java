package me.yejin.springboot3blogldap.config.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Jwts;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import me.yejin.springboot3blogldap.domain.LdapUser;
import me.yejin.springboot3blogldap.service.LdapUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

/**
 * author : yjseo
 * <p>
 * date : 2023-07-03
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("default")
public class TokenProviderTest {

  @Autowired
  private TokenProvider tokenProvider;
  @Autowired
  private JwtProperties jwtProperties;
  @Autowired
  private LdapUserService userService;

  @DisplayName("generateToken(): 유저 정보와 만료 기간을 전달해 토큰을 만드는 것에 성공한다.")
  @Test
  void generateToken() throws InvalidNameException {
    //given
    String dn = "uid=angel,ou=people";
    userService.createLdapUser(dn, "angel", "a123", "Angel Heart", "Heart");
    LdapUser createdUser = userService.findLdapUser("angel");

    //when
    String token = tokenProvider.generateToken(createdUser, Duration.ofDays(14));
    //then
    String resultDn = Jwts.parser()
        .setSigningKey(jwtProperties.getSecretKey())
        .parseClaimsJws(token)
        .getBody()
        .get("dn", String.class);

    assertThat(new LdapName(resultDn)).isEqualTo(createdUser.getDn());
  }

  @DisplayName("validToken(): 만료된 토큰일 때 유효성 검증에 실패한다.")
  @Test
  void validToken_fail() {
    //given
    String token = JwtFactory.builder()
        .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
        .build()
        .createToken(jwtProperties);

    //when
    boolean result = tokenProvider.validToken(token);

    //then
    assertThat(result).isFalse();
  }

  @DisplayName("validToken(): 유효한 토큰일 때 유효성 검증에 성공한다.")
  @Test
  void validToken_success() {
    //given
    String token = JwtFactory.withDefaultValues().createToken(jwtProperties);

    //when
    boolean result = tokenProvider.validToken(token);

    //then
    assertThat(result).isTrue();
  }

  @DisplayName("getAuthentication(): 토큰 기반으로 인증 정보를 가져오는 것에 성공한다.")
  @Test
  void getAuthentication() {
    //given
    String username = "krishna";
    String token = JwtFactory.builder()
        .subject(username)
        .build()
        .createToken(jwtProperties);

    //when
    Authentication authentication = tokenProvider.getAuthentication(token);

    //then
    assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(username);
  }

  @DisplayName("getUserId(): 토큰 기반으로 사용자 dn을 가져오는 것에 성공한다.")
  @Test
  void getUserId() {
    //given
    String dn = "uid=krishna,ou=people,dc=springframework,dc=org";
    String token = JwtFactory.builder()
        .claims(Map.of("dn", dn))
        .build()
        .createToken(jwtProperties);

    //when
    String dnByToken = tokenProvider.getUserDn(token);

    //then
    assertThat(dnByToken).isEqualTo(dn);
  }


}
