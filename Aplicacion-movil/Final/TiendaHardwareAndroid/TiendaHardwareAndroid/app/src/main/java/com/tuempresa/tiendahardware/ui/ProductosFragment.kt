package com.tuempresa.tiendahardware.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tuempresa.tiendahardware.data.CrearProductoRequest
import com.tuempresa.tiendahardware.data.ProductoDto
import com.tuempresa.tiendahardware.databinding.DialogProductoBinding
import com.tuempresa.tiendahardware.databinding.FragmentProductosBinding
import com.tuempresa.tiendahardware.net.ApiClient
import com.tuempresa.tiendahardware.store.CartStore
import com.tuempresa.tiendahardware.store.TokenStore
import kotlinx.coroutines.launch

class ProductosFragment : Fragment() {

  private var _b: FragmentProductosBinding? = null
  private val b get() = _b!!

  private lateinit var tokenStore: TokenStore
  private lateinit var api: ApiClient
  private lateinit var adapter: ProductosAdapter

  private var isAdmin: Boolean = false

  companion object {
    fun newInstance(isAdmin: Boolean): ProductosFragment {
      val f = ProductosFragment()
      f.arguments = Bundle().apply { putBoolean("isAdmin", isAdmin) }
      return f
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    isAdmin = arguments?.getBoolean("isAdmin") ?: false
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    _b = FragmentProductosBinding.inflate(inflater, container, false)
    return b.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    tokenStore = TokenStore(requireContext())
    api = ApiClient(tokenStore)

    adapter = ProductosAdapter(
      isAdmin = isAdmin,
      onAdd = { p -> CartStore.add(p); snack("Agregado al carrito") },
      onEdit = { p -> showProductoDialog(edit = p) },
      onDelete = { p -> bajaProducto(p) }
    )

    b.recycler.layoutManager = LinearLayoutManager(requireContext())
    b.recycler.adapter = adapter

    b.adminActions.visibility = if (isAdmin) View.VISIBLE else View.GONE
    b.btnAgregar.setOnClickListener { showProductoDialog(edit = null) }

    load()
  }

  private fun load() {
    b.progress.visibility = View.VISIBLE
    lifecycleScope.launch {
      try {
        val res = api.api.productos()
        if (!res.ok || res.data == null) {
          snack(res.error ?: "Error cargando productos")
          return@launch
        }
        adapter.submit(res.data.productos)
      } catch (e: Exception) {
        snack(e.message ?: "Error")
      } finally {
        b.progress.visibility = View.GONE
      }
    }
  }

  private fun showProductoDialog(edit: ProductoDto?) {
    val bind = DialogProductoBinding.inflate(layoutInflater)
    if (edit != null) {
      bind.inTipo.setText(edit.tipo)
      bind.inMarca.setText(edit.marca)
      bind.inCapacidad.setText(edit.capacidad_almacenamiento ?: "")
      bind.inPrecio.setText(edit.precio.toString())
      bind.inStock.setText(edit.stock.toString())
    }

    AlertDialog.Builder(requireContext())
      .setTitle(if (edit == null) "Alta de producto" else "Editar producto")
      .setView(bind.root)
      .setPositiveButton("Guardar") { _, _ ->
        val tipo = bind.inTipo.text?.toString()?.trim().orEmpty()
        val marca = bind.inMarca.text?.toString()?.trim().orEmpty()
        val cap = bind.inCapacidad.text?.toString()?.trim().orEmpty().ifBlank { null }
        val precio = bind.inPrecio.text?.toString()?.trim().orEmpty().toDoubleOrNull()
        val stock = bind.inStock.text?.toString()?.trim().orEmpty().toIntOrNull()

        if (tipo.isBlank() || marca.isBlank() || precio == null || stock == null) {
          snack("Completa tipo, marca, precio y stock")
          return@setPositiveButton
        }
        if (precio < 0 || stock < 0) {
          snack("Precio/Stock inválidos")
          return@setPositiveButton
        }

        val req = CrearProductoRequest(marca = marca, tipo = tipo, capacidad_almacenamiento = cap, precio = precio, stock = stock)

        if (edit == null) crearProducto(req) else updateProducto(edit.id, req)
      }
      .setNegativeButton("Cancelar", null)
      .show()
  }

  private fun crearProducto(req: CrearProductoRequest) {
    b.progress.visibility = View.VISIBLE
    lifecycleScope.launch {
      try {
        val res = api.api.adminCrearProducto(req)
        if (!res.ok) snack(res.error ?: "No se pudo crear") else snack("Producto creado")
        load()
      } catch (e: Exception) {
        snack(e.message ?: "Error")
      } finally {
        b.progress.visibility = View.GONE
      }
    }
  }

  private fun updateProducto(id: Long, req: CrearProductoRequest) {
    b.progress.visibility = View.VISIBLE
    lifecycleScope.launch {
      try {
        val res = api.api.adminUpdateProducto(id, req)
        if (!res.ok) snack(res.error ?: "No se pudo actualizar") else snack("Producto actualizado")
        load()
      } catch (e: Exception) {
        snack(e.message ?: "Error")
      } finally {
        b.progress.visibility = View.GONE
      }
    }
  }

  private fun bajaProducto(p: ProductoDto) {
    AlertDialog.Builder(requireContext())
      .setTitle("Baja lógica")
      .setMessage("¿Dar de baja el producto #${p.id}?")
      .setPositiveButton("Sí") { _, _ ->
        b.progress.visibility = View.VISIBLE
        lifecycleScope.launch {
          try {
            val res = api.api.adminDeleteProducto(p.id)
            if (!res.ok) snack(res.error ?: "No se pudo dar de baja") else snack("Producto dado de baja")
            load()
          } catch (e: Exception) {
            snack(e.message ?: "Error")
          } finally {
            b.progress.visibility = View.GONE
          }
        }
      }
      .setNegativeButton("No", null)
      .show()
  }

  private fun snack(msg: String) {
    Snackbar.make(b.root, msg, Snackbar.LENGTH_SHORT).show()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _b = null
  }
}
