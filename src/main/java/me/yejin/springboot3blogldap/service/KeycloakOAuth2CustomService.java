package me.yejin.springboot3blogldap.service;

import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import me.yejin.springboot3blogldap.config.oauth.OAuthAttributes;
import me.yejin.springboot3blogldap.domain.User;
import me.yejin.springboot3blogldap.dto.SessionUser;
import me.yejin.springboot3blogldap.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

/**
 * author : yejin
 * <p>
 * date : 2023-08-22
 */
@RequiredArgsConstructor
@Service
public class KeycloakOAuth2CustomService extends OidcUserService {

  private final UserRepository userRepository;

  private final HttpSession httpSession;

  @Override
  public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
    OidcUser oidcUser = super.loadUser(userRequest);

    String registrationId = userRequest.getClientRegistration().getRegistrationId();

    // 구글: sub, 네이버: response, 카카오: id
    String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

    OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oidcUser.getAttributes());

    User user = saveOrUpdate(attributes);
    httpSession.setAttribute("user", new SessionUser(oidcUser));

    System.out.println(attributes.getAttributes());
    return new DefaultOidcUser(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        , oidcUser.getIdToken(), oidcUser.getUserInfo(), attributes.getNameAttributeKey());
  }

  private User saveOrUpdate(OAuthAttributes attributes) {
    User user = userRepository.findByEmail(attributes.getEmail())
        .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
        .orElse(User.builder()
            .email(attributes.getEmail())
            .nickname(attributes.getName())
            .build());

    return userRepository.save(user);
  }
}
