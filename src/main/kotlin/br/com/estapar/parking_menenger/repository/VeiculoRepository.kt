package br.com.estapar.parking_menenger.repository

import br.com.estapar.parking_menenger.model.Veiculo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VeiculoRepository : JpaRepository<Veiculo, Long>