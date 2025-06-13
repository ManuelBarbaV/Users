package com.example.demo.Controller;

import com.example.demo.Client.UserClient;
import com.example.demo.DTO.ClientUserListDTO;
import com.example.demo.DTO.RefreshTokenRequest;
import com.example.demo.Model.TokenManager;
import com.example.demo.Model.UserCredentials;
import com.example.demo.Model.User;
import com.example.demo.Service.UserService;
import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;

@RestController
@RequestMapping("/usuarios")
public class UserController {

    @Autowired
    UserClient userClient;

    @Autowired
    UserService userService;

    @Autowired
    TokenManager tokenManager;



    @PostMapping("/login")
    public User login(@RequestBody @Valid UserCredentials credentials) {

        try{
            User user = userClient.login(credentials);

            if(user.getAccessToken() == null || user.getAccessToken().isBlank()){
                throw new RuntimeException("Token invalido en la respuesta");
            }

            tokenManager.updateTokens(user);

            try{

                userClient.auth("Bearer " + tokenManager.getAccessToken());
            }catch (FeignException.Unauthorized ex){


                RefreshTokenRequest refreshRequest = new RefreshTokenRequest(
                        tokenManager.getRefreshToken(), 30);
                User refreshedUser = userClient.refresh(refreshRequest);


                tokenManager.updateTokens(refreshedUser);


                userClient.auth("Bearer " + tokenManager.getAccessToken());


                user.setAccessToken(refreshedUser.getAccessToken());
                user.setRefreshToken(refreshedUser.getRefreshToken());
            }

            user.setLoginTime(new Timestamp(System.currentTimeMillis()));


            userService.create(user);

            return user;

        }catch (FeignException.BadRequest ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credenciales incorrectas.");
        }catch (FeignException.Unauthorized ex){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autorizado.");
        }catch (FeignException ex){
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error al comunicarse con el server.");
        }
    }


    @GetMapping("/all")
    public ClientUserListDTO getAllUsers() {
        return userClient.obtainUserList();
    }
}
