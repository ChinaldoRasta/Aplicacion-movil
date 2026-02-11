package com.tienda.hardware.security;

import com.tienda.hardware.repo.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository users;

  public CustomUserDetailsService(UserRepository users) {
    this.users = users;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var u = users.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    return org.springframework.security.core.userdetails.User
      .withUsername(u.getEmail())
      .password(u.getPasswordHash())
      .roles(u.getRol().name())
      .build();
  }
}
