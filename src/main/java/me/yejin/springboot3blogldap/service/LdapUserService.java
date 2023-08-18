package me.yejin.springboot3blogldap.service;

import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import lombok.extern.slf4j.Slf4j;
import me.yejin.springboot3blogldap.domain.LdapUser;
import me.yejin.springboot3blogldap.domain.User;
import me.yejin.springboot3blogldap.dto.SessionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * author : yjseo
 * <p>
 * date : 2023-07-03
 */
@Slf4j
@Service
@Transactional
public class LdapUserService implements UserDetailsService {
  @Autowired
  private HttpSession httpSession;
  @Autowired
  private LdapTemplate ldapTemplate;

  private LdapContextSource contextSource;

  private String searchBase;
  private String dnPattern;
  private String userId;
  private String userCommanName;
  private String userPassword;

  public LdapUserService(LdapTemplate ldapTemplate) {
    this.userId = "uid";
    this.contextSource = (LdapContextSource) ldapTemplate.getContextSource();
    this.searchBase = contextSource.getBaseLdapPathAsString();
    this.dnPattern =userId+"={0},ou=users";
    this.userCommanName = "cn";
    this.userPassword = this.contextSource.getPassword();
  }

  public LdapUser findLdapUser(String uid) {
    LdapQuery qry = LdapQueryBuilder.query().where("uid").is(uid);
    return ldapTemplate.findOne(qry, LdapUser.class);
  }

  public LdapUser findByEmail(String email) {
    LdapQuery qry = LdapQueryBuilder.query().where("mail").is(email);
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

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    LdapQuery qry = LdapQueryBuilder.query().where(userId).is(username);
    List<LdapUser> users = ldapTemplate.search(qry, new LdapUserMapper());
    if (users.isEmpty()) {
      throw new UsernameNotFoundException("User not found with username: " + username);
    }
    LdapUser user = users.get(0);
    httpSession.setAttribute("user", new SessionUser(user));
    return new User(user.getEmail(), user.getPassword(), username);
  }

  //Mapping the LDAP attributes.
  private class LdapUserMapper implements AttributesMapper<LdapUser> {
    @Override
    public LdapUser mapFromAttributes(Attributes attributes) throws NamingException {
      LdapUserDetailsImpl.Essence essence = new LdapUserDetailsImpl.Essence();
      try {
        essence.setUsername(attributes.get(userCommanName).get().toString());
        essence.setDn(dnPattern+","+searchBase);
        essence.setPassword(userPassword);
      }catch (Exception e) {
        log.info("Inside Catch Block of LdapUserDetailsMapper");
        e.printStackTrace();
      }
      essence.setAuthorities(Collections.emptyList());
      LdapUserDetails ldapUserDetails = essence.createUserDetails();
      LdapUser ldapUser = LdapUser.builder()
          .dn(ldapUserDetails.getDn())
          .uid(attributes.get(userId).get().toString())
          .userPassword(ldapUserDetails.getPassword())
          .cn(ldapUserDetails.getUsername())
          .sn(attributes.get("sn").get().toString())
          .email(attributes.get("mail").get().toString())
          .build();
      return ldapUser;
    }

  }
}
