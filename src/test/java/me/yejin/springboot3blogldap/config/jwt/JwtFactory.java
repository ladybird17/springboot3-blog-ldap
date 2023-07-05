package me.yejin.springboot3blogldap.config.jwt;

import static java.util.Collections.emptyMap;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

/**
 * author : yjseo
 * <p>
 * date : 2023-07-03
 */
@Getter
public class JwtFactory {
  private String subject = "krishna";
  private Date issuedAt = new Date();
  private Date expiration = new Date(new Date().getTime() + Duration.ofDays(14).toMillis());
  private Map<String, Object> claims = emptyMap();

  @Builder //원하는 객체에만 값을 지정하여 생성
  public JwtFactory(String subject, Date issuedAt, Date expiration, Map<String, Object> claims) {
    this.subject = subject != null ? subject : this.subject;
    this.issuedAt = issuedAt != null ? issuedAt : this.issuedAt;
    this.expiration = expiration != null ? expiration : this.expiration;
    this.claims = claims != null ? claims : this.claims;
  }

  public static JwtFactory withDefaultValues() {
    return JwtFactory.builder().build();
  }

  public String createToken(JwtProperties jwtProperties){
    return Jwts.builder()
        .setSubject(subject)
        .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
        .setIssuer(jwtProperties.getIssuer())
        .setIssuedAt(issuedAt)
        .setExpiration(expiration)
        .addClaims(claims)
        .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
        .compact();
  }
}
