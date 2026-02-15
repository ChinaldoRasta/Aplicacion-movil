package com.tuempresa.tiendahardware.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.tuempresa.tiendahardware.databinding.ActivityLoginBinding
import com.tuempresa.tiendahardware.data.LoginRequest
import com.tuempresa.tiendahardware.net.ApiClient
import com.tuempresa.tiendahardware.store.TokenStore
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

  private lateinit var b: ActivityLoginBinding
  private lateinit var tokenStore: TokenStore
  private lateinit var api: ApiClient

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    b = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(b.root)

    tokenStore = TokenStore(this)
    api = ApiClient(tokenStore)

    // Demo
    b.email.setText("user@tienda.com")
    b.password.setText("cliente123")

    b.btnLogin.setOnClickListener { doLogin() }
  }

  private fun doLogin() {
    val email = b.email.text?.toString()?.trim().orEmpty()
    val pass = b.password.text?.toString().orEmpty()

    if (email.isBlank() || pass.isBlank()) {
      showMsg("Completa email y contraseña")
      return
    }

    b.msg.visibility = View.GONE
    b.progress.visibility = View.VISIBLE
    b.btnLogin.isEnabled = false

    lifecycleScope.launch {
      try {
        val res = api.api.login(LoginRequest(email, pass))
        if (!res.ok || res.data == null) {
          showMsg(res.error ?: "No se pudo iniciar sesión")
          return@launch
        }
        val token = res.data.token
        val user = res.data.user
        tokenStore.setSession(token, user.rol, user.nombre)

        val next = if (user.rol == "ADMIN") Intent(this@LoginActivity, AdminActivity::class.java)
                   else Intent(this@LoginActivity, ClientActivity::class.java)
        startActivity(next)
        finish()
      } catch (e: Exception) {
        showMsg(e.message ?: "Error")
      } finally {
        b.progress.visibility = View.GONE
        b.btnLogin.isEnabled = true
      }
    }
  }

  private fun showMsg(msg: String) {
    b.msg.text = msg
    b.msg.visibility = View.VISIBLE
    Snackbar.make(b.root, msg, Snackbar.LENGTH_SHORT).show()
  }
}
