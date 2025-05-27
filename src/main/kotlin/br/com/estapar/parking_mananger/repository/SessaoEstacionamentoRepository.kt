package br.com.estapar.parking_mananger.repository

import br.com.estapar.parking_mananger.model.SessaoEstacionamento
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SessaoEstacionamentoRepository : JpaRepository<SessaoEstacionamento, Long> {

    fun findByVeiculoPlacaAndEstaAtivaTrue(placa: String): SessaoEstacionamento?

    fun findByVagaIdAndEstaAtivaTrue(vagaId: Long): SessaoEstacionamento?

    fun findAllByHorarioSaidaBetweenAndSetorNome(start: LocalDateTime, end: LocalDateTime, setorNome: String): List<SessaoEstacionamento>

}