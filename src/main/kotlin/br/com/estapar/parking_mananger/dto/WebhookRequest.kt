package br.com.estapar.parking_mananger.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class WebhookRequest(
    @JsonProperty("license_plate")
    val placaVeiculo: String,
    @JsonProperty("entry_time")
    val horarioEntrada: String? = null,
    @JsonProperty("exit_time")
    val horarioSaida: String? = null,
    @JsonProperty("event_type")
    val tipoEvento: String,
    @JsonProperty("lat")
    val latitude: Double? = null,
    @JsonProperty("lng")
    val longitude: Double? = null
)

