package me.yejin.springboot3blogldap.config.oauth;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import me.yejin.springboot3blogldap.domain.User;

/**
 * author : yejin
 * <p>
 * date : 2023-08-11
 */
@Getter
@Setter
public class OAuthAttributes {
  private Map<String, Object> attributes;
  private String nameAttributeKey;
  private String name;
  private String email;
  private String picture;

  public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture) {
    this.attributes = attributes;
    this.nameAttributeKey = nameAttributeKey;
    this.name = name;
    this.email = email;
    this.picture = picture;
  }

  public OAuthAttributes() {
  }

  public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
    if (registrationId.equals("kakao")) {
      return ofKakao(userNameAttributeName, attributes);
    } else if (registrationId.equals("naver")) {
      return ofNaver(userNameAttributeName,attributes);
    }
    return ofGoogle(userNameAttributeName, attributes);
  }

  private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
    Map<String, Object> kakao_account = (Map<String, Object>) attributes.get("kakao_account");
    Map<String, Object> profile = (Map<String, Object>) kakao_account.get("profile");

    return new OAuthAttributes(attributes,
        userNameAttributeName,
        (String) profile.get("nickname"),
        (String) kakao_account.get("email"),
        (String) profile.get("profile_image_url"));
  }

  private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
    Map<String, Object> response = (Map<String, Object>) attributes.get("response");

    return new OAuthAttributes(attributes,
        userNameAttributeName,
        (String) response.get("name"),
        (String) response.get("email"),
        (String) response.get("profile_image"));
  }

  private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {

    return new OAuthAttributes(attributes,
        userNameAttributeName,
        (String) attributes.get("name"),
        (String) attributes.get("email"),
        (String) attributes.get("picture"));
  }

  public User toEntity() {
    return new User(name, email, picture);
  }
}