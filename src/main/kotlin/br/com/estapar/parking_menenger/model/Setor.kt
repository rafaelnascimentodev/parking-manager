package br.com.estapar.parking_menenger.model

import jakarta.persistence.*
import java.time.LocalTime

@Entity
@Table(name = "setores_tb")
data class Setor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val nome: String,

    val precoBase: Double,

    val capacidadeMaxima: Int,

    val horarioAbertura: LocalTime,

    val horarioFechamento: LocalTime,

    val limiteDuracaoMinutos: Int,

    var ocupacaoAtual: Int = 0,

    var estaFechado: Boolean = false
)