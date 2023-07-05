package me.yejin.springboot3blogldap.domain;

import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.ldap.LdapName;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
/**
 * author : yjseo
 * <p>
 * date : 2023-07-03
 */
@NoArgsConstructor
@Data
@Entry(
    base = "ou=users",
    objectClasses = { "person", "top", "organizationalPerson", "inetOrgPerson" })
public final class LdapUser {

  @Id
  private Name dn;

  @Attribute(name="uid")
  @DnAttribute(value="uid", index=0)
  private String uid;

  @Attribute(name="userPassword")
  private String userPassword;

  @Attribute(name="cn")
  private String cn;

  @Attribute(name="sn")
  private String sn;

  @Builder
  public LdapUser(String dn, String uid, String userPassword, String cn, String sn) throws InvalidNameException {
    this.dn = new LdapName(dn);
    this.uid = uid;
    this.userPassword = userPassword;
    this.cn = cn;
    this.sn = sn;
  }

}
