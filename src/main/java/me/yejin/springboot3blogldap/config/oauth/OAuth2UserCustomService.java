package me.yejin.springboot3blogldap.config.oauth;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import me.yejin.springboot3blogldap.domain.User;
import me.yejin.springboot3blogldap.dto.SessionUser;
import me.yejin.springboot3blogldap.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;


/**
 * author : yejin
 * <p>
 * date : 2023-08-02
 */
@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;

  private final HttpSession httpSession;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest); // 요청을 바탕으로 유저 정보를 담은 객체 반환

    String registrationId = userRequest.getClientRegistration().getRegistrationId();

    // 구글: sub, 네이버: response, 카카오: id
    String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

    OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

    User user = saveOrUpdate(attributes);
    httpSession.setAttribute("user", new SessionUser(user));

    System.out.println(attributes.getAttributes());
    return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        , attributes.getAttributes()
        , attributes.getNameAttributeKey());
  }

  // 유저가 있으면 업데이트, 없으면 유저 생성
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