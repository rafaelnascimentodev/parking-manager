package br.com.estapar.parking_mananger.service

import br.com.estapar.parking_mananger.dto.GarageResponse
import br.com.estapar.parking_mananger.config.StartupFlag
import br.com.estapar.parking_mananger.model.Setor
import br.com.estapar.parking_mananger.model.Vaga
import br.com.estapar.parking_mananger.repository.SetorRepository
import br.com.estapar.parking_mananger.repository.VagaRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.web.client.RestTemplate
import java.time.LocalTime

@Component
class GarageImportService(
    private val restTemplate: RestTemplate,
    private val setorRepository: SetorRepository,
    private val vagaRepository: VagaRepository,
    private val startupFlag: StartupFlag
) {
    private val log: Logger = LoggerFactory.getLogger(GarageImportService::class.java)

    @Value("\${garage.api-url}")
    private lateinit var garageApiUrl: String

    @EventListener(ApplicationReadyEvent::class)
    fun importarDadosGaragem() {
        try {
            log.info("Iniciando importação dos dados da garagem...")
            val response = restTemplate.getForEntity(garageApiUrl, GarageResponse::class.java)
            val garagem = response.body ?: throw RuntimeException("Resposta da garagem vazia")

            log.info("Setores encontrados: ${garagem.garage.size}")

            // Salva setores e guarda em mapa para associar depois
            val setoresSalvos = mutableMapOf<String, Setor>()
            garagem.garage.forEach { setorDto ->
                val setor = Setor(
                    nome = setorDto.sector,
                    precoBase = setorDto.base_price,
                    capacidadeMaxima = setorDto.max_capacity,
                    horarioAbertura = LocalTime.parse(setorDto.open_hour),
                    horarioFechamento = LocalTime.parse(setorDto.close_hour),
                    limiteDuracaoMinutos = setorDto.duration_limit_minutes,
                )
                val setorSalvo = setorRepository.save(setor)
                setoresSalvos[setorSalvo.nome] = setorSalvo
                log.info("Setor '${setorSalvo.nome}' importado")
            }

            // Salva as vagas associadas ao setor correto
            garagem.spots.forEach { vagaDto ->
                val setor = setoresSalvos[vagaDto.sector]
                    ?: throw RuntimeException("Setor '${vagaDto.sector}' da vaga ${vagaDto.id} não encontrado")

                val vaga = Vaga(
                    setor = setor,
                    latitude = vagaDto.lat,
                    longitude = vagaDto.lng,
                    ocupada = vagaDto.occupied
                )
                vagaRepository.save(vaga)
            }

            startupFlag.sistemaPronto = true
            log.info("Importação concluída, sistema pronto para uso.")
        } catch (e: Exception) {
            log.error("Erro ao importar dados da garagem: ", e)
        }
    }
}
