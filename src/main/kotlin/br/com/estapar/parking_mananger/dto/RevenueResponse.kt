package br.com.estapar.parking_mananger.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class RevenueResponse(

    @JsonProperty("amount")
    val amount: Double,
    @JsonProperty("currency")
    val currency: String = "BRL",
    @JsonProperty("timestamp")
    val timestamp: LocalDateTime

)