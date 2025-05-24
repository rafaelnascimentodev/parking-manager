package br.com.estapar.parking_menenger.repository

import br.com.estapar.parking_menenger.model.Faturamento
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SetorRepository : JpaRepository<Faturamento, Long>