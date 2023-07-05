package me.yejin.springboot3blogldap.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * author : yjseo
 * <p>
 * date : 2023-07-03
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserViewControllerTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  private WebApplicationContext context;

  @BeforeEach
  public void mockMvcSetup(){
    this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
        .apply(springSecurity()).build();
  }

  @DisplayName("loginSuccess: LDAP 사용자 로그인에 성공한다.")
  @Test
  public void loginSuccess() throws Exception{

    //given
    final String url = "/login";
    final String redirectUrl = "/articles";
    final String username = "krishna";
    final String password = "k123";

    //when
    ResultActions result = mockMvc.perform(formLogin(url).user(username).password(password))
        .andDo(print());


    //then
    result.andExpect(authenticated())
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(redirectUrl));
  }
}