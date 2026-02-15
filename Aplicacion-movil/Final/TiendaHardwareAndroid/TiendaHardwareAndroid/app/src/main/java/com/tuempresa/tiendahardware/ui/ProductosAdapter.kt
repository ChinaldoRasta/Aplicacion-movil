package com.tuempresa.tiendahardware.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.tiendahardware.data.ProductoDto
import com.tuempresa.tiendahardware.databinding.ItemProductoBinding

class ProductosAdapter(
  private val isAdmin: Boolean,
  private val onAdd: (ProductoDto) -> Unit,
  private val onEdit: (ProductoDto) -> Unit,
  private val onDelete: (ProductoDto) -> Unit
) : RecyclerView.Adapter<ProductosAdapter.VH>() {

  private val items = mutableListOf<ProductoDto>()

  fun submit(list: List<ProductoDto>) {
    items.clear()
    items.addAll(list)
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    val b = ItemProductoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return VH(b)
  }

  override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
  override fun getItemCount(): Int = items.size

  inner class VH(private val b: ItemProductoBinding) : RecyclerView.ViewHolder(b.root) {
    fun bind(p: ProductoDto) {
      b.txtTipo.text = p.tipo
      b.txtMarca.text = "Marca: ${p.marca}"
      b.txtCapacidad.text = "Capacidad: ${p.capacidad_almacenamiento ?: "N/A"}"
      b.txtPrecio.text = "$%.2f".format(p.precio)
      b.txtStock.text = "Stock: ${p.stock}"

      if (isAdmin) {
        b.btnAddCart.visibility = View.GONE
        b.btnEdit.visibility = View.VISIBLE
        b.btnDelete.visibility = View.VISIBLE
      } else {
        b.btnAddCart.visibility = View.VISIBLE
        b.btnEdit.visibility = View.GONE
        b.btnDelete.visibility = View.GONE
      }

      b.btnAddCart.setOnClickListener { onAdd(p) }
      b.btnEdit.setOnClickListener { onEdit(p) }
      b.btnDelete.setOnClickListener { onDelete(p) }
    }
  }
}
