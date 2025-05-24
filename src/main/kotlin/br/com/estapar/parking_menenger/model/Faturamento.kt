package br.com.estapar.parking_menenger.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "faturamentos_tb")
data class Faturamento(
    @Id
    val id: String,

    val setor: String,

    val data: LocalDate,

    val valor: Double
)
