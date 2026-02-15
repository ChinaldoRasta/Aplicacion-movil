package com.tuempresa.tiendahardware.store

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class TokenStore(ctx: Context) {
  private val prefs = EncryptedSharedPreferences.create(
    "secure_prefs",
    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
    ctx,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
  )

  fun setSession(token: String, rol: String, nombre: String) {
    prefs.edit().putString("token", token)
      .putString("rol", rol)
      .putString("nombre", nombre)
      .apply()
  }

  fun clear() {
    prefs.edit().clear().apply()
  }

  fun getToken(): String? = prefs.getString("token", null)
  fun getRol(): String? = prefs.getString("rol", null)
  fun getNombre(): String? = prefs.getString("nombre", null)
}
