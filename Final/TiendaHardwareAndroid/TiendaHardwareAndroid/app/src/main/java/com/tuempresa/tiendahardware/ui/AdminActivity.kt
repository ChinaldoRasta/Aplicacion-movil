package com.tuempresa.tiendahardware.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.tuempresa.tiendahardware.databinding.ActivityAdminBinding
import com.tuempresa.tiendahardware.store.TokenStore

class AdminActivity : AppCompatActivity() {

  private lateinit var b: ActivityAdminBinding
  private lateinit var tokenStore: TokenStore

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    b = ActivityAdminBinding.inflate(layoutInflater)
    setContentView(b.root)

    tokenStore = TokenStore(this)

    b.toolbar.setOnMenuItemClickListener { item ->
      when (item.itemId) {
        android.R.id.home -> true
        else -> false
      }
    }
    b.toolbar.inflateMenu(com.tuempresa.tiendahardware.R.menu.menu_top)
    b.toolbar.setOnMenuItemClickListener {
      if (it.itemId == com.tuempresa.tiendahardware.R.id.action_logout) {
        tokenStore.clear()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
        true
      } else false
    }

    b.tabs.addTab(b.tabs.newTab().setText("Productos"))
    b.tabs.addTab(b.tabs.newTab().setText("Ventas"))

    supportFragmentManager.beginTransaction()
      .replace(b.container.id, ProductosFragment.newInstance(isAdmin = true))
      .commit()

    b.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
      override fun onTabSelected(tab: TabLayout.Tab) {
        val frag = if (tab.position == 0) ProductosFragment.newInstance(true) else VentasFragment()
        supportFragmentManager.beginTransaction().replace(b.container.id, frag).commit()
      }
      override fun onTabUnselected(tab: TabLayout.Tab) {}
      override fun onTabReselected(tab: TabLayout.Tab) {}
    })
  }
}
