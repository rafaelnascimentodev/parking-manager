package br.com.estapar.parking_mananger.service

import br.com.estapar.parking_mananger.config.StartupFlag
import br.com.estapar.parking_mananger.dto.GarageResponse
import br.com.estapar.parking_mananger.dto.SetorDTO
import br.com.estapar.parking_mananger.dto.VagaDTO
import br.com.estapar.parking_mananger.model.Setor
import br.com.estapar.parking_mananger.model.Vaga
import br.com.estapar.parking_mananger.repository.SetorRepository
import br.com.estapar.parking_mananger.repository.VagaRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.time.LocalTime

class GarageImportServiceTest {

    private lateinit var restTemplate: RestTemplate
    private lateinit var setorRepository: SetorRepository
    private lateinit var vagaRepository: VagaRepository
    private lateinit var startupFlag: StartupFlag
    private lateinit var garageImportService: GarageImportService

    private val garageApiUrl = "http://fake-api/garage"

    @BeforeEach
    fun setUp() {
        restTemplate = mock(RestTemplate::class.java)
        setorRepository = mock(SetorRepository::class.java)
        vagaRepository = mock(VagaRepository::class.java)
        startupFlag = StartupFlag()

        garageImportService = GarageImportService(restTemplate, setorRepository, vagaRepository, startupFlag)
        val garageApiUrlField = GarageImportService::class.java.getDeclaredField("garageApiUrl")
        garageApiUrlField.isAccessible = true
        garageApiUrlField.set(garageImportService, garageApiUrl)
    }

    @Test
    fun `deve importar dados corretamente e marcar sistema como pronto`() {
        val setorDTO = SetorDTO(
            sector = "A",
            base_price = 10.0,
            max_capacity = 5,
            open_hour = "08:00",
            close_hour = "18:00",
            duration_limit_minutes = 120
        )
        val vagaDTO = VagaDTO(
            id = 1,
            lat = 1.0,
            lng = 2.0,
            occupied = false,
            sector = "A"
        )

        val response = GarageResponse(garage = listOf(setorDTO), spots = listOf(vagaDTO))
        val setorSalvo = Setor(
            id = 1,
            nome = setorDTO.sector,
            precoBase = setorDTO.base_price,
            capacidadeMaxima = setorDTO.max_capacity,
            horarioAbertura = LocalTime.parse(setorDTO.open_hour),
            horarioFechamento = LocalTime.parse(setorDTO.close_hour),
            limiteDuracaoMinutos = setorDTO.duration_limit_minutes
        )

        `when`(restTemplate.getForEntity(garageApiUrl, GarageResponse::class.java))
            .thenReturn(ResponseEntity.ok(response))
        `when`(setorRepository.save(any(Setor::class.java))).thenReturn(setorSalvo)

        garageImportService.importarDadosGaragem()

        verify(setorRepository).save(any(Setor::class.java))
        verify(vagaRepository).save(any(Vaga::class.java))
        assert(startupFlag.sistemaPronto)
    }

    @Test
    fun `deve logar erro se resposta for nula`() {
        `when`(restTemplate.getForEntity(garageApiUrl, GarageResponse::class.java))
            .thenReturn(ResponseEntity.ok(null))

        garageImportService.importarDadosGaragem()

        assert(!startupFlag.sistemaPronto)
        verify(setorRepository, never()).save(any())
        verify(vagaRepository, never()).save(any())
    }

    @Test
    fun `deve logar erro se API lançar exceção`() {
        `when`(restTemplate.getForEntity(garageApiUrl, GarageResponse::class.java))
            .thenThrow(RuntimeException("Erro na API"))

        garageImportService.importarDadosGaragem()

        assert(!startupFlag.sistemaPronto)
        verifyNoInteractions(setorRepository)
        verifyNoInteractions(vagaRepository)
    }

}
