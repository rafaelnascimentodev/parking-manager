package br.com.estapar.parking_menenger.repository

import br.com.estapar.parking_menenger.model.Vaga
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VagaRepository : JpaRepository<Vaga, Long>