package com.tienda.hardware.service;

import com.tienda.hardware.dto.ProductDto;
import com.tienda.hardware.model.Product;
import com.tienda.hardware.repo.ProductRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private final ProductRepository products;

  public ProductService(ProductRepository products) {
    this.products = products;
  }

  public List<Product> listPublic(String q) {
    if(q == null || q.isBlank()) return products.findByActivoTrue();
    return products.searchActive(q.trim());
  }

  public List<Product> listAdmin() {
    return products.findAll();
  }

  @Transactional
  public Product create(ProductDto dto) {
    Product p = new Product();
    p.setTipo(dto.tipo);
    p.setMarca(dto.marca);
    p.setCapacidadAlmacenamiento(dto.capacidadAlmacenamiento);
    p.setStock(dto.stock);
    p.setPrecio(dto.precio);
    p.setActivo(dto.activo != null ? dto.activo : true);
    return products.save(p);
  }

  @Transactional
  public Product update(Long id, ProductDto dto) {
    Product p = products.findById(id).orElseThrow(() -> new IllegalArgumentException("Producto no existe"));
    p.setTipo(dto.tipo);
    p.setMarca(dto.marca);
    p.setCapacidadAlmacenamiento(dto.capacidadAlmacenamiento);
    p.setStock(dto.stock);
    p.setPrecio(dto.precio);
    if(dto.activo != null) p.setActivo(dto.activo);
    return products.save(p);
  }

  @Transactional
  public void delete(Long id) {
    products.deleteById(id);
  }
}
