package com.example.demo.Client;

import com.example.demo.DTO.ClientUserListDTO;
import com.example.demo.DTO.RefreshTokenRequest;
import com.example.demo.Model.User;
import com.example.demo.Model.UserCredentials;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "userClient" , url = "https://dummyjson.com")
public interface UserClient {

    @GetMapping("/users")
    public ClientUserListDTO obtainUserList();

    @PostMapping("/auth/login")
    public User login(@RequestBody UserCredentials user);

    @GetMapping("/auth/me")
    public void auth(@RequestHeader("Authorization") String token);

    @PostMapping("/auth/refresh")
    User refresh(@RequestBody RefreshTokenRequest request);

}
