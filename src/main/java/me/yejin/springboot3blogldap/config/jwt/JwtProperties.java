package me.yejin.springboot3blogldap.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * author : yjseo
 * <p>
 * date : 2023-07-03
 */
@Setter
@Getter
@Component
@ConfigurationProperties("jwt")
public class JwtProperties {
  private String issuer;
  private String secretKey;
}
