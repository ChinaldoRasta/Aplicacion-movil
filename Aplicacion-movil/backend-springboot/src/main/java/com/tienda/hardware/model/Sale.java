package com.tienda.hardware.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventas")
public class Sale {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = true)
  @JoinColumn(name="cliente_id")
  private User cliente;

  @ManyToOne(optional = true)
  @JoinColumn(name="admin_id")
  private User admin;

  @Column(nullable = false)
  private Instant fecha = Instant.now();

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal total = BigDecimal.ZERO;

  @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SaleDetail> items = new ArrayList<>();

  public Long getId() { return id; }
  public User getCliente() { return cliente; }
  public void setCliente(User cliente) { this.cliente = cliente; }
  public User getAdmin() { return admin; }
  public void setAdmin(User admin) { this.admin = admin; }
  public Instant getFecha() { return fecha; }
  public BigDecimal getTotal() { return total; }
  public void setTotal(BigDecimal total) { this.total = total; }
  public List<SaleDetail> getItems() { return items; }
}
