package br.com.estapar.parking_mananger.service

import br.com.estapar.parking_mananger.model.SessaoEstacionamento
import br.com.estapar.parking_mananger.model.Veiculo
import br.com.estapar.parking_mananger.repository.SessaoEstacionamentoRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.time.LocalDateTime

class PlateStatusServiceTest {

    private val sessaoRepository: SessaoEstacionamentoRepository = mock()
    private val saidaService: SaidaService = mock()
    private val plateStatusService = PlateStatusService(sessaoRepository, saidaService)

    @Test
    fun `should return plate status when active session exists`() {
        val licensePlate = "ABC1234"
        val now = LocalDateTime.now()
        val entrada = now.minusHours(1).minusMinutes(30)
        val sessao = SessaoEstacionamento(
            id = 1L,
            veiculo = Veiculo(placa = licensePlate),
            vaga = mock(), // mockk ainda para dependências não diretamente testadas aqui
            setor = mock(),
            horarioEntrada = entrada,
            horarioSaida = null,
            estaAtiva = true,
            precoCobrado = null,
            precoBase = 10.0
        )
        val expectedPrice = 12.5

        `when`(sessaoRepository.findByVeiculoPlacaAndEstaAtivaTrue(licensePlate)).thenReturn(sessao)
        `when`(saidaService.calcularPrecoComRegra(10.0, 90)).thenReturn(expectedPrice)

        val result = plateStatusService.getPlateStatus(licensePlate)

        assertEquals(licensePlate, result?.licensePlate)
        assertEquals(expectedPrice, result?.priceUntilNow)
        assertEquals(entrada, result?.entryTime)
        assertEquals("01h 30m", result?.timeParked)
    }

    @Test
    fun `should return null when no active session found`() {
        val licensePlate = "XYZ9876"
        `when`(sessaoRepository.findByVeiculoPlacaAndEstaAtivaTrue(licensePlate)).thenReturn(null)

        val result = plateStatusService.getPlateStatus(licensePlate)

        assertNull(result)
    }
}