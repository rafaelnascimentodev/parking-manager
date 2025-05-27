package br.com.estapar.parking_mananger.service

import br.com.estapar.parking_mananger.dto.SpotStatusResponse
import br.com.estapar.parking_mananger.model.SessaoEstacionamento
import br.com.estapar.parking_mananger.model.Setor
import br.com.estapar.parking_mananger.model.Vaga
import br.com.estapar.parking_mananger.repository.SessaoEstacionamentoRepository
import br.com.estapar.parking_mananger.repository.VagaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@ExtendWith(MockitoExtension::class)
class SpotStatusServiceTest {

    @Mock
    lateinit var vagaRepository: VagaRepository

    @Mock
    lateinit var sessaoRepository: SessaoEstacionamentoRepository

    @InjectMocks
    lateinit var spotStatusService: SpotStatusService

    private val latitude = -23.0
    private val longitude = -46.0

    @Test
    fun `should return spot status as not occupied when vaga is not occupied`() {
        val vaga = Vaga(id = 1L, setor = mock(), latitude = latitude, longitude = longitude, ocupada = false)
        `when`(vagaRepository.findByLatitudeAndLongitude(latitude, longitude)).thenReturn(vaga)

        val expectedResponse = SpotStatusResponse(false, null, null)
        assertEquals(expectedResponse, spotStatusService.getSpotStatus(latitude, longitude))
    }

    @Test
    fun `should return spot status as occupied with details when vaga is occupied and session exists`() {
        val vaga = Vaga(id = 1L, setor = mock(), latitude = latitude, longitude = longitude, ocupada = true)
        val entradaFixo = LocalDateTime.of(2025, 5, 27, 10, 0, 0)
        val nowFixo = LocalDateTime.now()
        val duration = ChronoUnit.MINUTES.between(entradaFixo, nowFixo)
        val expectedTimeParked = String.format("%02dh %02dm", duration / 60, duration % 60)
        val sessao = SessaoEstacionamento(
            id = 10L,
            veiculo = mock(),
            vaga = vaga,
            setor = mock(),
            horarioEntrada = entradaFixo,
            horarioSaida = null,
            estaAtiva = true,
            precoCobrado = null,
            precoBase = 5.0
        )

        `when`(vagaRepository.findByLatitudeAndLongitude(latitude, longitude)).thenReturn(vaga)
        `when`(sessaoRepository.findByVagaIdAndEstaAtivaTrue(vaga.id)).thenReturn(sessao)

        val expectedResponse = SpotStatusResponse(true, entradaFixo, expectedTimeParked)
        assertEquals(expectedResponse, spotStatusService.getSpotStatus(latitude, longitude))
    }

    @Test
    fun `should return spot status as occupied but without details if no active session found for occupied vaga`() {
        val vaga = Vaga(id = 1L, setor = mock(), latitude = latitude, longitude = longitude, ocupada = true)
        `when`(vagaRepository.findByLatitudeAndLongitude(latitude, longitude)).thenReturn(vaga)
        `when`(sessaoRepository.findByVagaIdAndEstaAtivaTrue(vaga.id)).thenReturn(null)

        val expectedResponse = SpotStatusResponse(true, null, null)
        assertEquals(expectedResponse, spotStatusService.getSpotStatus(latitude, longitude))
    }

    @Test
    fun `should return null when vaga is not found`() {
        `when`(vagaRepository.findByLatitudeAndLongitude(latitude, longitude)).thenReturn(null)

        assertEquals(null, spotStatusService.getSpotStatus(latitude, longitude))
    }
}