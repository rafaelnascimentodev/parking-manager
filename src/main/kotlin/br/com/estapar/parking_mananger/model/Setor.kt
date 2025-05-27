package br.com.estapar.parking_mananger.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalTime

@Entity
@Table(name = "setores_tb")
data class Setor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val nome: String,  // corresponde ao "sector" do JSON

    @Column(nullable = false)
    val precoBase: Double,  // base_price no JSON

    @Column(nullable = false)
    val capacidadeMaxima: Int,  // max_capacity no JSON

    @Column(nullable = false)
    val horarioAbertura: LocalTime,  // open_hour no JSON

    @Column(nullable = false)
    val horarioFechamento: LocalTime,  // close_hour no JSON

    @Column(nullable = false)
    val limiteDuracaoMinutos: Int  // duration_limit_minutes no JSON
)
