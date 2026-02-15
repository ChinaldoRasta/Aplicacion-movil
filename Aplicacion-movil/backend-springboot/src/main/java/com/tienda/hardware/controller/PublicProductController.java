package com.tienda.hardware.controller;

import com.tienda.hardware.model.Product;
import com.tienda.hardware.service.ProductService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
public class PublicProductController {

  private final ProductService products;

  public PublicProductController(ProductService products) {
    this.products = products;
  }

  @GetMapping
  public List<Product> list(@RequestParam(required = false) String q) {
    return products.listPublic(q);
  }
}
