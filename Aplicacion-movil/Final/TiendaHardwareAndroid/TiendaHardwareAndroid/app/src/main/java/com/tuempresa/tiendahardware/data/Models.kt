package com.tuempresa.tiendahardware.data

data class ApiResponse<T>(
  val ok: Boolean,
  val data: T? = null,
  val error: String? = null
)

data class LoginRequest(val email: String, val password: String)

data class UserDto(
  val id: Int,
  val email: String,
  val nombre: String,
  val rol: String
)

data class LoginData(val token: String, val user: UserDto)

data class ProductosData(val productos: List<ProductoDto>)
data class ProductoData(val producto: ProductoDto)

data class ProductoDto(
  val id: Long,
  val marca: String,
  val tipo: String,
  val capacidad_almacenamiento: String? = null,
  val precio: Double,
  val stock: Int
)

data class CrearProductoRequest(
  val marca: String,
  val tipo: String,
  val capacidad_almacenamiento: String? = null,
  val precio: Double,
  val stock: Int
)

data class VentaItemReq(val producto_id: Long, val cantidad: Int)
data class CrearVentaRequest(val items: List<VentaItemReq>)
data class CrearVentaData(val venta_id: Long, val total: Double)

data class VentaDto(
  val id: Long,
  val fecha: String,
  val total: Double,
  val admin_nombre: String? = null,
  val cliente_nombre: String? = null
)
data class VentasData(val ventas: List<VentaDto>)
