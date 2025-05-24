package br.com.estapar.parking_mananger.repository

import br.com.estapar.parking_mananger.model.Setor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SetorRepository : JpaRepository<Setor, Long>