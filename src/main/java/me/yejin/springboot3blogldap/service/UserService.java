package me.yejin.springboot3blogldap.service;

import lombok.RequiredArgsConstructor;
import me.yejin.springboot3blogldap.domain.User;
import me.yejin.springboot3blogldap.dto.AddUserRequest;
import me.yejin.springboot3blogldap.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * author : yjseo
 * <p>
 * date : 2023-06-20
 */
@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;
  public Long save(AddUserRequest dto){
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    return userRepository.save(User.builder()
            .email(dto.getEmail())
            .password(encoder.encode(dto.getPassword()))
            .build()).getId();
  }

  public User findById(Long userId){
    return userRepository.findById(userId).orElseThrow(()->new IllegalArgumentException("Unexpected user"));
  }

  public User findByEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
  }
}
