package me.yejin.springboot3blogldap.service;

import javax.naming.InvalidNameException;
import lombok.RequiredArgsConstructor;
import me.yejin.springboot3blogldap.domain.LdapUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * author : yjseo
 * <p>
 * date : 2023-07-03
 */
@RequiredArgsConstructor
@Service
@Transactional
public class LdapUserService {

  @Autowired
  private LdapTemplate ldapTemplate;

  public LdapUser findLdapUser(String uid) {
    LdapQuery qry = LdapQueryBuilder.query().where("uid").is(uid);
    return ldapTemplate.findOne(qry, LdapUser.class);
  }

  public void createLdapUser(String dn, String uid, String password, String cn, String sn)
      throws InvalidNameException {
    Object createUser = LdapUser.builder()
        .dn(dn)
        .uid(uid)
        .userPassword(new LdapShaPasswordEncoder().encode(password))
        .cn(cn)
        .sn(sn)
        .build();

    ldapTemplate.create(createUser);
  }

}
