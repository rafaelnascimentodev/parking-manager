package br.com.estapar.parking_mananger.repository

import br.com.estapar.parking_mananger.model.Veiculo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VeiculoRepository : JpaRepository<Veiculo, String>