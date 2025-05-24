package br.com.estapar.parking_menenger.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table


@Entity
@Table(name = "veiculos_tb")
data class Veiculo(

    @Id
    val placa: String
)