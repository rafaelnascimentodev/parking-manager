package br.com.estapar.parking_mananger.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "sessao_estacionamento_tb")
data class SessaoEstacionamento(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    val veiculo: Veiculo,

    @ManyToOne
    val vaga: Vaga,

    @ManyToOne
    val setor: Setor,

    val horarioEntrada: LocalDateTime,

    var horarioSaida: LocalDateTime? = null,

    var estaAtiva: Boolean = true,

    var precoCobrado: Double? = null,

    val precoBase: Double
)
