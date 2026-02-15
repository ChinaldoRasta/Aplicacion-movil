package com.tienda.hardware.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class OrderDtos {

  public static class OrderItem {
    @NotNull public Long productoId;
    @NotNull @Min(1) public Integer cantidad;
  }

  public static class CreateOrderRequest {
    @NotNull @Size(min=1) public List<OrderItem> items;
  }

  public static class CreateSaleAdminRequest {
    public Long clienteId; // opcional
    @NotNull @Size(min=1) public List<OrderItem> items;
  }

  public static class SaleResponse {
    public Long id;
    public String fecha;
    public Long clienteId;
    public Long adminId;
    public String total;
    public List<SaleItemResponse> items;

    public static class SaleItemResponse {
      public Long productoId;
      public String tipo;
      public String marca;
      public Integer cantidad;
      public String precioUnitario;
      public String subtotal;
    }
  }
}
