package br.com.estapar.parking_mananger.service

import br.com.estapar.parking_mananger.dto.SpotStatusResponse
import br.com.estapar.parking_mananger.repository.SessaoEstacionamentoRepository
import br.com.estapar.parking_mananger.repository.VagaRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class SpotStatusService(
    private val vagaRepository: VagaRepository,
    private val sessaoRepository: SessaoEstacionamentoRepository
) {
    private val log = LoggerFactory.getLogger(SpotStatusService::class.java)

    fun getSpotStatus(latitude: Double, longitude: Double): SpotStatusResponse? {
        log.info("Consultando status da vaga: lat=$latitude, lng=$longitude")
        val vaga = vagaRepository.findByLatitudeAndLongitude(latitude, longitude) ?: return null

        if (!vaga.ocupada) {
            return SpotStatusResponse(occupied = false, entryTime = null, timeParked = null)
        } else {
            val sessaoAtiva = sessaoRepository.findByVagaIdAndEstaAtivaTrue(vaga.id)
            return sessaoAtiva?.let { sessao ->
                val now = LocalDateTime.now()
                val duration = ChronoUnit.MINUTES.between(sessao.horarioEntrada, now)
                val timeParkedFormatted = String.format("%02dh %02dm", duration / 60, duration % 60)
                SpotStatusResponse(occupied = true, entryTime = sessao.horarioEntrada, timeParked = timeParkedFormatted)
            } ?: SpotStatusResponse(occupied = true, entryTime = null, timeParked = null)
        }
    }
}