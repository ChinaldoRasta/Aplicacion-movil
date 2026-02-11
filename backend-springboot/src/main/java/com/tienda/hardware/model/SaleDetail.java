package com.tienda.hardware.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_venta")
public class SaleDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name="venta_id")
  private Sale venta;

  @ManyToOne(optional = false)
  @JoinColumn(name="producto_id")
  private Product producto;

  @Column(nullable = false)
  private Integer cantidad;

  @Column(name="precio_unitario", nullable = false, precision = 10, scale = 2)
  private BigDecimal precioUnitario;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal subtotal;

  public SaleDetail() {}

  public SaleDetail(Sale venta, Product producto, Integer cantidad, BigDecimal precioUnitario) {
    this.venta = venta;
    this.producto = producto;
    this.cantidad = cantidad;
    this.precioUnitario = precioUnitario;
    this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
  }

  public Long getId() { return id; }
  public Sale getVenta() { return venta; }
  public Product getProducto() { return producto; }
  public Integer getCantidad() { return cantidad; }
  public BigDecimal getPrecioUnitario() { return precioUnitario; }
  public BigDecimal getSubtotal() { return subtotal; }
}
