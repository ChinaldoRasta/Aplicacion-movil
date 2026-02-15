package com.tuempresa.tiendahardware.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.tiendahardware.databinding.ItemCartBinding
import com.tuempresa.tiendahardware.store.CartItem

class CartAdapter(
  private val onPlus: (com.tuempresa.tiendahardware.data.ProductoDto) -> Unit,
  private val onMinus: (com.tuempresa.tiendahardware.data.ProductoDto) -> Unit
) : RecyclerView.Adapter<CartAdapter.VH>() {

  private val items = mutableListOf<CartItem>()

  fun submit(list: List<CartItem>) {
    items.clear()
    items.addAll(list)
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    val b = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return VH(b)
  }

  override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
  override fun getItemCount(): Int = items.size

  inner class VH(private val b: ItemCartBinding) : RecyclerView.ViewHolder(b.root) {
    fun bind(it: CartItem) {
      val p = it.producto
      b.txtName.text = "${p.tipo} - ${p.marca}"
      b.txtPrice.text = "Precio: $%.2f  |  Subtotal: $%.2f".format(p.precio, it.subtotal)
      b.txtQty.text = it.cantidad.toString()

      b.btnPlus.setOnClickListener { onPlus(p) }
      b.btnMinus.setOnClickListener { onMinus(p) }
    }
  }
}
