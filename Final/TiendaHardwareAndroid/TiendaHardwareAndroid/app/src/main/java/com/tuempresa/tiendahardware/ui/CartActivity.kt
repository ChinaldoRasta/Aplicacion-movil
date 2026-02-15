package com.tuempresa.tiendahardware.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tuempresa.tiendahardware.data.CrearVentaRequest
import com.tuempresa.tiendahardware.data.VentaItemReq
import com.tuempresa.tiendahardware.databinding.ActivityCartBinding
import com.tuempresa.tiendahardware.net.ApiClient
import com.tuempresa.tiendahardware.store.CartStore
import com.tuempresa.tiendahardware.store.TokenStore
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {

  private lateinit var b: ActivityCartBinding
  private lateinit var tokenStore: TokenStore
  private lateinit var api: ApiClient
  private lateinit var adapter: CartAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    b = ActivityCartBinding.inflate(layoutInflater)
    setContentView(b.root)

    tokenStore = TokenStore(this)
    api = ApiClient(tokenStore)

    b.toolbar.setNavigationOnClickListener { finish() }
    b.toolbar.title = if (intent.getStringExtra("mode") == "ADMIN") "Registrar venta (Admin)" else "Carrito"

    adapter = CartAdapter(
      onPlus = { CartStore.add(it) ; refresh() },
      onMinus = { CartStore.remove(it) ; refresh() }
    )
    b.recycler.layoutManager = LinearLayoutManager(this)
    b.recycler.adapter = adapter

    b.btnConfirm.setOnClickListener { confirmar() }

    refresh()
  }

  private fun refresh() {
    adapter.submit(CartStore.items())
    b.txtTotal.text = "Total: $%.2f".format(CartStore.total())
  }

  private fun confirmar() {
    if (CartStore.items().isEmpty()) {
      snack("No hay productos en el carrito")
      return
    }

    val items = CartStore.items().map {
      VentaItemReq(producto_id = it.producto.id, cantidad = it.cantidad)
    }

    b.progress.visibility = View.VISIBLE
    b.btnConfirm.isEnabled = false

    lifecycleScope.launch {
      try {
        val res = api.api.crearVenta(CrearVentaRequest(items))
        if (!res.ok || res.data == null) {
          snack(res.error ?: "No se pudo registrar")
          return@launch
        }
        val id = res.data.venta_id
        val total = res.data.total
        CartStore.clear()
        refresh()
        snack("Venta #$id registrada. Total $%.2f".format(total))
        finish()
      } catch (e: Exception) {
        snack(e.message ?: "Error")
      } finally {
        b.progress.visibility = View.GONE
        b.btnConfirm.isEnabled = true
      }
    }
  }

  private fun snack(msg: String) {
    Snackbar.make(b.root, msg, Snackbar.LENGTH_SHORT).show()
  }
}
