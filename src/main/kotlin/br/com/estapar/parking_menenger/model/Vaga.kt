package br.com.estapar.parking_menenger.model

import jakarta.persistence.*

@Entity
@Table(name = "vagas_tb")
data class Vaga(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id", nullable = false)
    val setor: Setor,

    @Column(nullable = false)
    val latitude: Double,

    @Column(nullable = false)
    val longitude: Double,

    @Column(nullable = false)
    var ocupada: Boolean = false
)
