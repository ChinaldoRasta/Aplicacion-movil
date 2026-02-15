package com.tuempresa.tiendahardware.store

import com.tuempresa.tiendahardware.data.ProductoDto

data class CartItem(val producto: ProductoDto, var cantidad: Int = 1) {
  val subtotal: Double get() = producto.precio * cantidad
}

object CartStore {
  private val items = mutableListOf<CartItem>()

  fun items(): List<CartItem> = items

  fun add(p: ProductoDto) {
    val it = items.firstOrNull { it.producto.id == p.id }
    if (it == null) items.add(CartItem(p, 1)) else it.cantidad++
  }

  fun remove(p: ProductoDto) {
    val it = items.firstOrNull { it.producto.id == p.id } ?: return
    it.cantidad--
    if (it.cantidad <= 0) items.remove(it)
  }

  fun clear() = items.clear()

  fun total(): Double = items.sumOf { it.subtotal }
}
