package com.tuempresa.tiendahardware.net

import com.tuempresa.tiendahardware.AppConfig
import com.tuempresa.tiendahardware.store.TokenStore
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient(private val tokenStore: TokenStore) {

  private val authInterceptor = Interceptor { chain ->
    val reqBuilder = chain.request().newBuilder()
    val token = tokenStore.getToken()
    if (!token.isNullOrBlank()) {
      reqBuilder.addHeader("Authorization", "Bearer $token")
    }
    chain.proceed(reqBuilder.build())
  }

  private val logger = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BASIC
  }

  private val http = OkHttpClient.Builder()
    .addInterceptor(authInterceptor)
    .addInterceptor(logger)
    .build()

  private val retrofit = Retrofit.Builder()
    .baseUrl(AppConfig.BASE_URL)
    .client(http)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

  val api: ApiService = retrofit.create(ApiService::class.java)
}
