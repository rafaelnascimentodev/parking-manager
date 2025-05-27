package br.com.estapar.parking_mananger.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class PlateStatusResponse(
    @JsonProperty("license_plate")
    val licensePlate: String,
    @JsonProperty("price_until_now")
    val priceUntilNow: Double,
    @JsonProperty("entry_time")
    val entryTime: LocalDateTime?,
    @JsonProperty("time_parked")
    val timeParked: String?
)