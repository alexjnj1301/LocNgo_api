package com.locngo.controller;

import com.locngo.dto.JwtResponse;
import com.locngo.dto.LoginRequest;
import com.locngo.dto.RegisterRequest;
import com.locngo.dto.UserRegisterDto;
import com.locngo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  @Autowired private UserService userService;

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
    var jwt = userService.login(loginRequest);
    return ResponseEntity.ok(new JwtResponse(jwt));
  }

  @PostMapping("/register")
  public ResponseEntity<UserRegisterDto> register(@RequestBody RegisterRequest registerRequest) {
    var user = userService.register(registerRequest);
    var jwt =
        userService.login(new LoginRequest(registerRequest.email(), registerRequest.password()));

    return ResponseEntity.ok(new UserRegisterDto(user, jwt));
  }
}
