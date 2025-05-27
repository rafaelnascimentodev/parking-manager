package br.com.estapar.parking_mananger.service

import br.com.estapar.parking_mananger.dto.EntradaRequest
import br.com.estapar.parking_mananger.dto.SaidaRequest
import br.com.estapar.parking_mananger.dto.WebhookRequest
import br.com.estapar.parking_mananger.repository.SetorRepository
import br.com.estapar.parking_mananger.repository.VagaRepository
import org.apache.coyote.BadRequestException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter

@Service
class WebhookService(
    private val entradaService: EntradaService,
    private val saidaService: SaidaService,
    private val vagaRepository: VagaRepository,
    private val setorRepository: SetorRepository
) {
    private val log = LoggerFactory.getLogger(WebhookService::class.java)
    private val formatter = DateTimeFormatter.ISO_DATE_TIME

    fun processarEvento(request: WebhookRequest) {
        log.info("Processando evento webhook: $request")

        when (request.tipoEvento.uppercase()) {
            "ENTRY", "PARKED" -> {
                val placa = request.placaVeiculo ?: throw BadRequestException("license_plate é obrigatório")
                val lat = request.latitude ?: throw BadRequestException("lat é obrigatório")
                val lng = request.longitude ?: throw BadRequestException("lng é obrigatório")

                val vaga = vagaRepository.findByLatitudeAndLongitude(lat, lng)
                    ?: throw BadRequestException("Vaga não encontrada para lat=$lat, lng=$lng")

                val setor = setorRepository.findById(vaga.setor.id!!)
                    .orElseThrow { BadRequestException("Setor da vaga não encontrado") }

                val entradaRequest = EntradaRequest(
                    placa = placa,
                    vagaId = vaga.id!!,
                    setorId = setor.id!!
                )

                entradaService.registrarEntrada(entradaRequest)
            }

            "EXIT" -> {
                val placa = request.placaVeiculo ?: throw BadRequestException("license_plate é obrigatório")
                val saidaRequest = SaidaRequest(placa)
                saidaService.registrarSaida(saidaRequest)
            }

            else -> {
                throw BadRequestException("event_type inválido: ${request.tipoEvento}")
            }
        }
    }
}
