package com.example.helloworld.data

import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApi {
    @GET("v4/latest/{base}")
    suspend fun getRates(@Path("base") base: String): ExchangeRateResponse
}
