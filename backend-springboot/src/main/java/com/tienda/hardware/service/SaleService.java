package com.tienda.hardware.service;

import com.tienda.hardware.dto.OrderDtos;
import com.tienda.hardware.model.*;
import com.tienda.hardware.repo.*;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SaleService {

  private final SaleRepository sales;
  private final ProductRepository products;
  private final UserRepository users;

  public SaleService(SaleRepository sales, ProductRepository products, UserRepository users) {
    this.sales = sales;
    this.products = products;
    this.users = users;
  }

  @Transactional
  public Sale crearVentaCliente(String email, OrderDtos.CreateOrderRequest req) {
    User cliente = users.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Cliente no existe"));
    return crearVenta(req.items, cliente, null);
  }

  @Transactional
  public Sale crearVentaAdmin(Long clienteId, String adminEmail, OrderDtos.CreateSaleAdminRequest req) {
    User admin = users.findByEmail(adminEmail).orElseThrow();
    User cliente = null;
    if(clienteId != null) cliente = users.findById(clienteId).orElse(null);
    return crearVenta(req.items, cliente, admin);
  }

  private Sale crearVenta(List<OrderDtos.OrderItem> items, User cliente, User admin) {
    Sale venta = new Sale();
    venta.setCliente(cliente);
    venta.setAdmin(admin);

    BigDecimal total = BigDecimal.ZERO;
    List<SaleDetail> detalles = new ArrayList<>();

    for (var it : items) {
      Product p = products.findById(it.productoId).orElseThrow(() -> new IllegalArgumentException("Producto no existe: " + it.productoId));

      if(!Boolean.TRUE.equals(p.getActivo())) {
        throw new IllegalArgumentException("Producto inactivo: " + p.getId());
      }

      int stock = p.getStock() == null ? 0 : p.getStock();
      int cant = it.cantidad == null ? 0 : it.cantidad;

      if(cant <= 0) throw new IllegalArgumentException("Cantidad inválida");
      if(stock < cant) throw new IllegalArgumentException("Stock insuficiente para producto " + p.getId());

      // baja stock
      p.setStock(stock - cant);
      products.save(p);

      SaleDetail d = new SaleDetail(venta, p, cant, p.getPrecio());
      detalles.add(d);
      total = total.add(d.getSubtotal());
    }

    venta.setTotal(total);
    venta.getItems().addAll(detalles);

    return sales.save(venta);
  }

  public List<OrderDtos.SaleResponse> listarVentas() {
    var fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.of("UTC"));
    List<OrderDtos.SaleResponse> out = new ArrayList<>();
    for (Sale v : sales.findAll()) {
      OrderDtos.SaleResponse r = toResponse(v, fmt);
      out.add(r);
    }
    return out;
  }

  public OrderDtos.SaleResponse toResponse(Sale v, DateTimeFormatter fmt) {
    OrderDtos.SaleResponse r = new OrderDtos.SaleResponse();
    r.id = v.getId();
    r.fecha = fmt.format(v.getFecha());
    r.clienteId = v.getCliente() != null ? v.getCliente().getId() : null;
    r.adminId = v.getAdmin() != null ? v.getAdmin().getId() : null;
    r.total = v.getTotal().toPlainString();
    r.items = new ArrayList<>();
    for (SaleDetail d : v.getItems()) {
      OrderDtos.SaleResponse.SaleItemResponse it = new OrderDtos.SaleResponse.SaleItemResponse();
      it.productoId = d.getProducto().getId();
      it.tipo = d.getProducto().getTipo();
      it.marca = d.getProducto().getMarca();
      it.cantidad = d.getCantidad();
      it.precioUnitario = d.getPrecioUnitario().toPlainString();
      it.subtotal = d.getSubtotal().toPlainString();
      r.items.add(it);
    }
    return r;
  }
}
