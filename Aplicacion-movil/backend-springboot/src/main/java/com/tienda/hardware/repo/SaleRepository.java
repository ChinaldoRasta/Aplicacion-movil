package com.tienda.hardware.repo;

import com.tienda.hardware.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {}
