package com.example.helloworld.data

data class ExchangeRateResponse(
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)
