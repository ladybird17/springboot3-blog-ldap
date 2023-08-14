package me.yejin.springboot3blogldap.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import me.yejin.springboot3blogldap.domain.LdapUser;
import me.yejin.springboot3blogldap.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

/**
 * author : yjseo
 * <p>
 * date : 2023-07-03
 */
@RequiredArgsConstructor
@Service
public class TokenProvider {
  private final JwtProperties jwtProperties;

  public String generateToken(User user, Duration expiredAt){
    Date now = new Date();
    return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
  }

  public String generateLdapToken(LdapUser user, Duration expiredAt){
    Date now = new Date();
    return makeLdapToken(new Date(now.getTime() + expiredAt.toMillis()), user);
  }

  private String makeToken(Date expiry, User user) {
    Date now = new Date();

    return Jwts.builder()
        .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
        .setIssuer(jwtProperties.getIssuer())
        .setIssuedAt(now)
        .setExpiration(expiry)
        .setSubject(user.getEmail())
        .claim("id", user.getId())
        .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
        .compact();
  }

  private String makeLdapToken(Date expiry, LdapUser user) {
    Date now = new Date();

    return Jwts.builder()
        .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
        .setIssuer(jwtProperties.getIssuer())
        .setIssuedAt(now)
        .setExpiration(expiry)
        .setSubject(user.getEmail())
        .claim("dn", user.getDn().toString())
        .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
        .compact();
  }

  public boolean validToken(String token) {
    try{
      Jwts.parser()
            .setSigningKey(jwtProperties.getSecretKey()) //비밀값 사용해서 token 복호화
            .parseClaimsJws(token);
      return true;
    }
    catch (Exception e){
      return false;

    }
  }

  public Authentication getAuthentication(String token) {
    Claims claims = getClaims(token);
    Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

    return new UsernamePasswordAuthenticationToken(new org.springframework.
        security.core.userdetails.User(claims.getSubject(), "", authorities)
        , token, authorities);
  }

  public String getUserDn(String token){
    Claims claims = getClaims(token);
    return claims.get("dn", String.class);
  }

  private Claims getClaims(String token) {
    return Jwts.parser()
        .setSigningKey(jwtProperties.getSecretKey())
        .parseClaimsJws(token)
        .getBody();
  }
}
