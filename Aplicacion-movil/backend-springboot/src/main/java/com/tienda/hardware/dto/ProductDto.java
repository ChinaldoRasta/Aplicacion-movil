package com.tienda.hardware.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductDto {
  public Long id;

  @NotBlank public String tipo;
  @NotBlank public String marca;
  public String capacidadAlmacenamiento;

  @NotNull @Min(0) public Integer stock;
  @NotNull @DecimalMin("0.00") public BigDecimal precio;
  public Boolean activo = true;
}
