package com.tienda.hardware.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "productos")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 80)
  private String tipo;

  @Column(nullable = false, length = 120)
  private String marca;

  @Column(name="capacidad_almacenamiento", length = 80)
  private String capacidadAlmacenamiento;

  @Column(nullable = false)
  private Integer stock = 0;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal precio = BigDecimal.ZERO;

  @Column(nullable = false)
  private Boolean activo = true;

  @Column(name="creado_en", nullable = false, updatable = false)
  private Instant creadoEn = Instant.now();

  @Column(name="actualizado_en", nullable = false)
  private Instant actualizadoEn = Instant.now();

  @PreUpdate
  public void onUpdate() {
    actualizadoEn = Instant.now();
  }

  public Product() {}

  public Product(String tipo, String marca, String capacidadAlmacenamiento, Integer stock, BigDecimal precio) {
    this.tipo = tipo;
    this.marca = marca;
    this.capacidadAlmacenamiento = capacidadAlmacenamiento;
    this.stock = stock;
    this.precio = precio;
    this.activo = true;
  }

  public Long getId() { return id; }
  public String getTipo() { return tipo; }
  public void setTipo(String tipo) { this.tipo = tipo; }
  public String getMarca() { return marca; }
  public void setMarca(String marca) { this.marca = marca; }
  public String getCapacidadAlmacenamiento() { return capacidadAlmacenamiento; }
  public void setCapacidadAlmacenamiento(String capacidadAlmacenamiento) { this.capacidadAlmacenamiento = capacidadAlmacenamiento; }
  public Integer getStock() { return stock; }
  public void setStock(Integer stock) { this.stock = stock; }
  public BigDecimal getPrecio() { return precio; }
  public void setPrecio(BigDecimal precio) { this.precio = precio; }
  public Boolean getActivo() { return activo; }
  public void setActivo(Boolean activo) { this.activo = activo; }
}
