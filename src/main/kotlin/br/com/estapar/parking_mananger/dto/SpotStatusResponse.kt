package br.com.estapar.parking_mananger.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class SpotStatusResponse(
    @JsonProperty("occupied")
    val occupied: Boolean,
    @JsonProperty("entry_time")
    val entryTime: LocalDateTime?,
    @JsonProperty("time_parked")
    val timeParked: String?
)