package br.com.estapar.parking_mananger.dto

data class EntradaRequest(

    val placa: String,
    val vagaId: Long,
    val setorId: Long

)
