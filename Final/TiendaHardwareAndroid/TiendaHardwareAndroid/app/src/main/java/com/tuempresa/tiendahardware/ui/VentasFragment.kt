package com.tuempresa.tiendahardware.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tuempresa.tiendahardware.databinding.FragmentVentasBinding
import com.tuempresa.tiendahardware.net.ApiClient
import com.tuempresa.tiendahardware.store.TokenStore
import kotlinx.coroutines.launch

class VentasFragment : Fragment() {

  private var _b: FragmentVentasBinding? = null
  private val b get() = _b!!

  private lateinit var tokenStore: TokenStore
  private lateinit var api: ApiClient
  private lateinit var adapter: VentasAdapter

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    _b = FragmentVentasBinding.inflate(inflater, container, false)
    return b.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    tokenStore = TokenStore(requireContext())
    api = ApiClient(tokenStore)

    adapter = VentasAdapter()
    b.recycler.layoutManager = LinearLayoutManager(requireContext())
    b.recycler.adapter = adapter

    b.btnNuevaVenta.setOnClickListener {
      startActivity(Intent(requireContext(), CartActivity::class.java).apply {
        putExtra("mode", "ADMIN") // mismo flujo: crea venta usando /ventas
      })
    }

    load()
  }

  private fun load() {
    b.progress.visibility = View.VISIBLE
    lifecycleScope.launch {
      try {
        val res = api.api.adminVentas()
        if (!res.ok || res.data == null) {
          snack(res.error ?: "No se pudieron cargar ventas")
          return@launch
        }
        adapter.submit(res.data.ventas)
      } catch (e: Exception) {
        snack(e.message ?: "Error")
      } finally {
        b.progress.visibility = View.GONE
      }
    }
  }

  private fun snack(msg: String) {
    Snackbar.make(b.root, msg, Snackbar.LENGTH_SHORT).show()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _b = null
  }
}
