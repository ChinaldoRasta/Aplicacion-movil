package com.tienda.hardware.service;

import com.tienda.hardware.dto.AuthDtos;
import com.tienda.hardware.model.Role;
import com.tienda.hardware.model.User;
import com.tienda.hardware.repo.UserRepository;
import com.tienda.hardware.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final AuthenticationManager authManager;
  private final UserRepository users;
  private final PasswordEncoder encoder;
  private final JwtService jwt;

  public AuthService(AuthenticationManager authManager, UserRepository users, PasswordEncoder encoder, JwtService jwt) {
    this.authManager = authManager;
    this.users = users;
    this.encoder = encoder;
    this.jwt = jwt;
  }

  public String login(AuthDtos.LoginRequest req) {
    authManager.authenticate(new UsernamePasswordAuthenticationToken(req.email, req.password));
    var u = users.findByEmail(req.email).orElseThrow();
    return jwt.generateToken(u.getEmail(), u.getRol().name());
  }

  @Transactional
  public void registerCliente(AuthDtos.RegisterRequest req) {
    if(users.existsByEmail(req.email)) {
      throw new IllegalArgumentException("El email ya existe");
    }
    User u = new User(req.nombre, req.email, encoder.encode(req.password), Role.CLIENTE);
    users.save(u);
  }
}
