package br.com.estapar.parking_mananger.repository

import br.com.estapar.parking_mananger.model.Vaga
import org.springframework.data.jpa.repository.JpaRepository

interface VagaRepository : JpaRepository<Vaga, Long> {

    fun countBySetorId(setorId: Long): Long
    fun countBySetorIdAndOcupadaTrue(setorId: Long): Long

}
