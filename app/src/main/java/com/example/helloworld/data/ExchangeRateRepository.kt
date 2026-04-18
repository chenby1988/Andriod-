package com.example.helloworld.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ExchangeRateRepository {
    private const val BASE_URL = "https://api.exchangerate-api.com/"

    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }

    private val api: ExchangeRateApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExchangeRateApi::class.java)
    }

    suspend fun getRates(base: String = "USD"): Result<ExchangeRateResponse> {
        return try {
            val response = api.getRates(base)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
