package com.tienda.hardware.controller;

import com.tienda.hardware.dto.ProductDto;
import com.tienda.hardware.model.Product;
import com.tienda.hardware.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/productos")
public class AdminProductController {

  private final ProductService products;

  public AdminProductController(ProductService products) {
    this.products = products;
  }

  @GetMapping
  public List<Product> list() {
    return products.listAdmin();
  }

  @PostMapping
  public ResponseEntity<Product> create(@Valid @RequestBody ProductDto dto) {
    return ResponseEntity.ok(products.create(dto));
  }

  @PutMapping("/<built-in function id>")
  public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody ProductDto dto) {
    return ResponseEntity.ok(products.update(id, dto));
  }

  @DeleteMapping("/<built-in function id>")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    products.delete(id);
    return ResponseEntity.ok().build();
  }
}
