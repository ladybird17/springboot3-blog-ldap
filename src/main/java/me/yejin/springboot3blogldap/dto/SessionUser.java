package me.yejin.springboot3blogldap.dto;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.yejin.springboot3blogldap.domain.LdapUser;
import me.yejin.springboot3blogldap.domain.User;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

/**
 * author : yejin
 * <p>
 * date : 2023-08-11
 */
@Data
@NoArgsConstructor
public class SessionUser implements Serializable {
  private String name;
  private String email;
  private String picture;

  public SessionUser(User user) {
    this.name = user.getNickname();
    this.email = user.getEmail();
    this.picture = user.getPicture();
  }

  public SessionUser(LdapUser user) {
    this.name = user.getCn();
    this.email = user.getEmail();
    this.picture = null;
  }

  public SessionUser(OidcUser oidcUser){
    this.name = oidcUser.getName();
    this.email = oidcUser.getEmail();
    this.picture = oidcUser.getPicture();
  }

}