package br.com.estapar.parking_menenger.dto

data class EntradaRequest(

    val placa: String,
    val vagaId: Long,
    val setorId: Long

)
