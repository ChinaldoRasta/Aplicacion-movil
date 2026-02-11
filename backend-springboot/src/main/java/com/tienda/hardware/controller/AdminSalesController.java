package com.tienda.hardware.controller;

import com.tienda.hardware.dto.OrderDtos;
import com.tienda.hardware.service.SaleService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/ventas")
public class AdminSalesController {

  private final SaleService sales;

  public AdminSalesController(SaleService sales) {
    this.sales = sales;
  }

  @GetMapping
  public List<OrderDtos.SaleResponse> list() {
    return sales.listarVentas();
  }

  @PostMapping
  public ResponseEntity<?> create(@Valid @RequestBody OrderDtos.CreateSaleAdminRequest req, Principal principal) {
    var venta = sales.crearVentaAdmin(req.clienteId, principal.getName(), req);
    return ResponseEntity.ok(sales.toResponse(venta, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(java.time.ZoneId.of("UTC"))));
  }
}
