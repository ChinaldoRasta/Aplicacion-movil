package com.tienda.hardware.controller;

import com.tienda.hardware.dto.AuthDtos;
import com.tienda.hardware.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService auth;

  public AuthController(AuthService auth) {
    this.auth = auth;
  }

  @PostMapping("/login")
  public ResponseEntity<AuthDtos.LoginResponse> login(@Valid @RequestBody AuthDtos.LoginRequest req) {
    String token = auth.login(req);
    return ResponseEntity.ok(new AuthDtos.LoginResponse(token));
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody AuthDtos.RegisterRequest req) {
    auth.registerCliente(req);
    return ResponseEntity.ok().build();
  }
}
