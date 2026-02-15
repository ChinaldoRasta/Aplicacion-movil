package com.tuempresa.tiendahardware.net

import com.tuempresa.tiendahardware.data.*
import retrofit2.http.*

interface ApiService {

  @POST("auth/login")
  suspend fun login(@Body req: LoginRequest): ApiResponse<LoginData>

  @GET("productos")
  suspend fun productos(): ApiResponse<ProductosData>

  // Admin productos
  @POST("admin/productos")
  suspend fun adminCrearProducto(@Body req: CrearProductoRequest): ApiResponse<Map<String, Any>>

  @PUT("admin/productos/{id}")
  suspend fun adminUpdateProducto(@Path("id") id: Long, @Body req: CrearProductoRequest): ApiResponse<Map<String, Any>>

  @DELETE("admin/productos/{id}")
  suspend fun adminDeleteProducto(@Path("id") id: Long): ApiResponse<Map<String, Any>>

  // Ventas (cliente y admin)
  @POST("ventas")
  suspend fun crearVenta(@Body req: CrearVentaRequest): ApiResponse<CrearVentaData>

  // Admin ver ventas
  @GET("admin/ventas")
  suspend fun adminVentas(): ApiResponse<VentasData>
}
