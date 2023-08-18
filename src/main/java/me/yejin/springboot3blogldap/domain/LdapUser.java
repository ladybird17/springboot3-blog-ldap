package me.yejin.springboot3blogldap.domain;

import java.util.Collection;
import java.util.List;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.ldap.LdapName;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapUserDetails;

/**
 * author : yjseo
 * <p>
 * date : 2023-07-03
 */
@NoArgsConstructor
@Getter
@Entry(
    base = "ou=users",
    objectClasses = { "top","inetOrgPerson" })
public final class LdapUser implements LdapUserDetails {

  @Id
  private Name dn;

  @Attribute(name="uid")
  @DnAttribute(value="uid", index=1)
  private String uid;

  @Attribute(name="userPassword")
  private String userPassword;

  @Attribute(name="cn")
  private String cn;

  @Attribute(name="sn")
  private String sn;

  @Attribute(name="mail")
  private String email;

  @Builder
  public LdapUser(String dn, String uid, String userPassword, String cn, String sn, String email) throws InvalidNameException {
    this.dn = new LdapName(dn);
    this.uid = uid;
    this.userPassword = userPassword;
    this.cn = cn;
    this.sn = sn;
    this.email = email;
  }

  @Override
  public void eraseCredentials() {
    this.userPassword = null;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("user"));
  }

  @Override
  public String getDn() {
    return this.dn.toString();
  }

  @Override
  public String getPassword() {
    return this.userPassword;
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
