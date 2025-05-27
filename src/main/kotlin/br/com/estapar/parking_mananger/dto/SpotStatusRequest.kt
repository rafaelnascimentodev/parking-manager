package br.com.estapar.parking_mananger.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SpotStatusRequest(
    @JsonProperty("lat")
    val latitude: Double,
    @JsonProperty("lng")
    val longitude: Double
)