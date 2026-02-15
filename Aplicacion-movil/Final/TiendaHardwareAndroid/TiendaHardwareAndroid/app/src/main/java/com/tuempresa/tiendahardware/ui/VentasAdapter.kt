package com.tuempresa.tiendahardware.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.tiendahardware.data.VentaDto
import com.tuempresa.tiendahardware.databinding.ItemVentaBinding

class VentasAdapter : RecyclerView.Adapter<VentasAdapter.VH>() {

  private val items = mutableListOf<VentaDto>()

  fun submit(list: List<VentaDto>) {
    items.clear()
    items.addAll(list)
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    val b = ItemVentaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return VH(b)
  }

  override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
  override fun getItemCount(): Int = items.size

  inner class VH(private val b: ItemVentaBinding) : RecyclerView.ViewHolder(b.root) {
    fun bind(v: VentaDto) {
      b.txtTitle.text = "Venta #${v.id}  -  $%.2f".format(v.total)
      val admin = v.admin_nombre ?: "N/A"
      val cliente = v.cliente_nombre ?: "N/A"
      b.txtSubtitle.text = "${v.fecha}\nAdmin: $admin  |  Cliente: $cliente"
    }
  }
}
