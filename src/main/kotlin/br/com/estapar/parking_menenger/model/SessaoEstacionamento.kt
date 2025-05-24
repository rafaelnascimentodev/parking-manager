package br.com.estapar.parking_menenger.model

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "sessao_estacionamento_tb")
data class SessaoEstacionamento (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_placa")
    val veiculo: Veiculo,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaga_id")
    val vaga: Vaga,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id")
    val setor: Setor,

    val horarioEntrada: LocalDateTime,

    var horarioEstacionado: LocalDateTime? = null,

    var horarioSaida: LocalDateTime? = null,

    var precoCobrado: Double? = null,

    var estaAtiva: Boolean = true
)