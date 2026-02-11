package com.tienda.hardware.controller;

import com.tienda.hardware.dto.OrderDtos;
import com.tienda.hardware.service.SaleService;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ordenes")
public class ClientOrderController {

  private final SaleService sales;

  public ClientOrderController(SaleService sales) {
    this.sales = sales;
  }

  @PostMapping
  public ResponseEntity<?> create(@Valid @RequestBody OrderDtos.CreateOrderRequest req, Principal principal) {
    var venta = sales.crearVentaCliente(principal.getName(), req);
    return ResponseEntity.ok(sales.toResponse(venta, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(java.time.ZoneId.of("UTC"))));
  }
}
