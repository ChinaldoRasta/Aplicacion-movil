package com.tuempresa.tiendahardware.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tuempresa.tiendahardware.R
import com.tuempresa.tiendahardware.databinding.ActivityClientBinding
import com.tuempresa.tiendahardware.store.TokenStore

class ClientActivity : AppCompatActivity() {

  private lateinit var b: ActivityClientBinding
  private lateinit var tokenStore: TokenStore

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    b = ActivityClientBinding.inflate(layoutInflater)
    setContentView(b.root)

    tokenStore = TokenStore(this)

    val nombre = tokenStore.getNombre().orEmpty()
    b.toolbar.title = if (nombre.isBlank()) "Productos" else "Hola, $nombre"
    b.toolbar.inflateMenu(R.menu.menu_client)
    b.toolbar.setOnMenuItemClickListener {
      when (it.itemId) {
        R.id.action_cart -> { startActivity(Intent(this, CartActivity::class.java)); true }
        R.id.action_logout -> {
          tokenStore.clear()
          startActivity(Intent(this, LoginActivity::class.java))
          finish()
          true
        }
        else -> false
      }
    }

    supportFragmentManager.beginTransaction()
      .replace(b.container.id, ProductosFragment.newInstance(isAdmin = false))
      .commit()
  }
}
