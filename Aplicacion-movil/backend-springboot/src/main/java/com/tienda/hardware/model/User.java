package com.tienda.hardware.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "usuarios")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 120)
  private String nombre;

  @Column(nullable = false, unique = true, length = 160)
  private String email;

  @Column(name="password_hash", nullable = false, length = 255)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role rol = Role.CLIENTE;

  @Column(name="creado_en", nullable = false, updatable = false)
  private Instant creadoEn = Instant.now();

  public User() {}

  public User(String nombre, String email, String passwordHash, Role rol) {
    this.nombre = nombre;
    this.email = email;
    this.passwordHash = passwordHash;
    this.rol = rol;
  }

  public Long getId() { return id; }
  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
  public Role getRol() { return rol; }
  public void setRol(Role rol) { this.rol = rol; }
  public Instant getCreadoEn() { return creadoEn; }
}
