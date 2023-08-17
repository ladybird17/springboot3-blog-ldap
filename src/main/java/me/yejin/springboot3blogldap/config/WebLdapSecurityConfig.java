package me.yejin.springboot3blogldap.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;

/**
 * author : yejin
 * <p>
 * date : 2023-08-17
 */
@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class WebLdapSecurityConfig {

  @Autowired
  public void configure(AuthenticationManagerBuilder auth) throws Exception {

    auth
        .ldapAuthentication()
        .userDnPatterns("uid={0},ou=users")
        .groupSearchBase("ou=groups")
        .contextSource()
        .url("ldap://localhost:389/dc=ldap,dc=yejin,dc=co,dc=kr")
        .managerDn("cn=admin,dc=ldap,dc=yejin,dc=co,dc=kr")
        .managerPassword("admin")
        .and()
        .passwordCompare()
        .passwordEncoder(new LdapShaPasswordEncoder())
        .passwordAttribute("userPassword");
  }

  @Bean
  public LdapTemplate ldapTemplate() {
    return new LdapTemplate(contextSource());
  }

  @Bean
  public LdapContextSource contextSource() {
    LdapContextSource contextSource = new LdapContextSource();

    contextSource.setUrl("ldap://localhost:389");
    contextSource.setBase("dc=ldap,dc=yejin,dc=co,dc=kr");
    contextSource.setUserDn("cn=admin,dc=ldap,dc=yejin,dc=co,dc=kr");
    contextSource.setPassword("admin");

    return contextSource;
  }
}
