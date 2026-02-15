package com.tienda.hardware.repo;

import com.tienda.hardware.model.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query("select p from Product p where p.activo=true and (lower(p.tipo) like lower(concat('%', :q, '%')) or lower(p.marca) like lower(concat('%', :q, '%')))")
  List<Product> searchActive(String q);

  List<Product> findByActivoTrue();
}
