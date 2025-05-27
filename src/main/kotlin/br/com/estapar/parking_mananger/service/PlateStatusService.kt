package br.com.estapar.parking_mananger.service

import br.com.estapar.parking_mananger.dto.PlateStatusResponse
import br.com.estapar.parking_mananger.repository.SessaoEstacionamentoRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Service
class PlateStatusService(
    private val sessaoRepository: SessaoEstacionamentoRepository,
    private val saidaService: SaidaService
) {
    private val log = LoggerFactory.getLogger(PlateStatusService::class.java)
    private val formatter = DateTimeFormatter.ISO_DATE_TIME

    fun getPlateStatus(licensePlate: String): PlateStatusResponse? {
        log.info("Consultando status da placa: $licensePlate")
        val sessaoAtiva = sessaoRepository.findByVeiculoPlacaAndEstaAtivaTrue(licensePlate)

        return sessaoAtiva?.let { sessao ->
            val now = LocalDateTime.now()
            val duration = ChronoUnit.MINUTES.between(sessao.horarioEntrada, now)
            val price = saidaService.calcularPrecoComRegra(sessao.precoBase, duration)
            val timeParkedFormatted = formatDuration(duration)

            PlateStatusResponse(
                licensePlate = licensePlate,
                priceUntilNow = price,
                entryTime = sessao.horarioEntrada,
                timeParked = timeParkedFormatted
            )
        }
    }

    private fun formatDuration(minutes: Long): String {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return String.format("%02dh %02dm", hours, remainingMinutes)
    }

}