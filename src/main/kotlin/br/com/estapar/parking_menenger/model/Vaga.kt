package br.com.estapar.parking_menenger.model

import jakarta.persistence.*

@Entity
@Table(name = "vagas_tb")
data class Vaga(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id")
    val setor: Setor,

    val latitude: Double,

    val longitude: Double,

    var ocupada: Boolean = false
)
