package br.com.estapar.parking_mananger.service

import br.com.estapar.parking_mananger.dto.RevenueResponse
import br.com.estapar.parking_mananger.repository.SessaoEstacionamentoRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class RevenueService(
    private val sessaoRepository: SessaoEstacionamentoRepository
) {
    private val log = LoggerFactory.getLogger(RevenueService::class.java)

    fun getRevenue(date: LocalDate, sectorName: String): RevenueResponse {
        log.info("Consultando faturamento para data=$date e setor=$sectorName")

        val startOfDay = date.atStartOfDay()
        val endOfDay = date.plusDays(1).atStartOfDay().minusNanos(1)

        val faturamentoDoDia = sessaoRepository.findAllByHorarioSaidaBetweenAndSetorNome(startOfDay, endOfDay, sectorName)
            .filter { it.precoCobrado != null }
            .sumOf { it.precoCobrado!! }

        return RevenueResponse(
            amount = faturamentoDoDia,
            timestamp = LocalDateTime.now()
        )
    }
}