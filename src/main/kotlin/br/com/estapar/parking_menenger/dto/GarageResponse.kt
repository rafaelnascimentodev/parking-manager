package br.com.estapar.parking_menenger.dto

data class GarageResponse(
    val garage: List<SetorDTO>,
    val spots: List<VagaDTO>
)

data class SetorDTO(
    val sector: String,
    val base_price: Double,
    val max_capacity: Int,
    val open_hour: String,
    val close_hour: String,
    val duration_limit_minutes: Int
)

data class VagaDTO(
    val id: Long,
    val sector: String,
    val lat: Double,
    val lng: Double,
    val occupied: Boolean
)


