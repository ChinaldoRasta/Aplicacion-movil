package com.tienda.hardware.dto;

import jakarta.validation.constraints.*;

public class AuthDtos {

  public static class LoginRequest {
    @NotBlank @Email public String email;
    @NotBlank public String password;
  }

  public static class LoginResponse {
    public String token;
    public LoginResponse(String token) { this.token = token; }
  }

  public static class RegisterRequest {
    @NotBlank @Size(min=2,max=120) public String nombre;
    @NotBlank @Email public String email;
    @NotBlank @Size(min=6,max=72) public String password;
  }
}
