package com.example.demo;

import com.example.demo.Client.UserClient;
import com.example.demo.Controller.UserController;
import com.example.demo.DTO.RefreshTokenRequest;
import com.example.demo.Model.TokenManager;
import com.example.demo.Model.User;
import com.example.demo.Service.UserService;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @MockBean
    private UserService userService;

    @MockBean
    private TokenManager tokenManager;

    @Test
    void testLoginSuccess() throws Exception {
        User user = new User();
        user.setAccessToken("access-token");
        user.setRefreshToken("refresh-token");
        user.setUsername("kminchelle");

        when(userClient.login(any())).thenReturn(user);
        doNothing().when(userClient).auth("Bearer access-token");
        when(userService.create(any())).thenReturn(user);

        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "kminchelle",
                                  "password": "0lelplR"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("kminchelle"))
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));

        verify(tokenManager).updateTokens(any(User.class));
    }

    @Test
    void testLoginWithExpiredTokenThenRefresh() throws Exception {
        User initialUser = new User();
        initialUser.setAccessToken("expired-token");
        initialUser.setRefreshToken("refresh-token");
        initialUser.setUsername("kminchelle");

        User refreshedUser = new User();
        refreshedUser.setAccessToken("new-access-token");
        refreshedUser.setRefreshToken("new-refresh-token");
        refreshedUser.setUsername("kminchelle");

        when(userClient.login(any())).thenReturn(initialUser);

        Request request = Request.create(Request.HttpMethod.GET, "/auth/me",
                java.util.Collections.emptyMap(), null, new RequestTemplate());

        doThrow(new FeignException.Unauthorized("Unauthorized", request, null, null))
                .when(userClient).auth("Bearer expired-token");

        when(userClient.refresh(any(RefreshTokenRequest.class))).thenReturn(refreshedUser);
        doNothing().when(userClient).auth("Bearer new-access-token");
        when(userService.create(any())).thenReturn(refreshedUser);

        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "kminchelle",
                                  "password": "0lelplR"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("kminchelle"))
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));

        verify(tokenManager).updateTokens(initialUser);
        verify(tokenManager).updateTokens(refreshedUser);
    }
}
