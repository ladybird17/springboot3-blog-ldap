package me.yejin.springboot3blogldap.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import me.yejin.springboot3blogldap.config.jwt.TokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * author : yjseo
 * <p>
 * date : 2023-07-03
 */
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
  private final TokenProvider tokenProvider;

  private final static String HEADER_AUTHORIZATION = "Authorization";
  private final static String TOKEN_PREFIX = "Bearer ";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)  throws ServletException, IOException {

    String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
    String token = getAccessToken(authorizationHeader);

    if (tokenProvider.validToken(token)) {
      Authentication authentication = tokenProvider.getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(authentication); //인증 정보 설정
    }

    filterChain.doFilter(request, response);
  }

  private String getAccessToken(String authorizationHeader) {
    if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
      return authorizationHeader.substring(TOKEN_PREFIX.length());
    }

    return null;
  }
}

