package br.com.estapar.parking_mananger.service

import br.com.estapar.parking_mananger.dto.RevenueResponse
import br.com.estapar.parking_mananger.model.SessaoEstacionamento
import br.com.estapar.parking_mananger.model.Setor
import br.com.estapar.parking_mananger.repository.SessaoEstacionamentoRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.time.LocalDate
import java.time.LocalDateTime

class RevenueServiceTest {

    private val sessaoRepository: SessaoEstacionamentoRepository = mock()
    private val revenueService = RevenueService(sessaoRepository)

    @Test
    fun `should return total revenue for a given date and sector`() {
        val date = LocalDate.of(2025, 5, 27)
        val sectorName = "A"
        val startOfDay = date.atStartOfDay()
        val endOfDay = date.plusDays(1).atStartOfDay().minusNanos(1)

        val setorA = Setor(nome = sectorName, precoBase = 10.0, capacidadeMaxima = 100, horarioAbertura = java.time.LocalTime.MIN, horarioFechamento = java.time.LocalTime.MAX, limiteDuracaoMinutos = 240)

        val sessoes = listOf(
            SessaoEstacionamento(id = 1L, veiculo = mock(), vaga = mock(), setor = setorA, horarioEntrada = startOfDay.plusHours(1), horarioSaida = startOfDay.plusHours(2), estaAtiva = false, precoCobrado = 15.0, precoBase = 10.0),
            SessaoEstacionamento(id = 2L, veiculo = mock(), vaga = mock(), setor = setorA, horarioEntrada = startOfDay.plusHours(3), horarioSaida = startOfDay.plusHours(4), estaAtiva = false, precoCobrado = 20.0, precoBase = 10.0),
            SessaoEstacionamento(id = 3L, veiculo = mock(), vaga = mock(), setor = setorA, horarioEntrada = startOfDay.minusDays(1), horarioSaida = startOfDay.minusDays(1).plusHours(1), estaAtiva = false, precoCobrado = 10.0, precoBase = 10.0) // Fora do dia
        )

        `when`(sessaoRepository.findAllByHorarioSaidaBetweenAndSetorNome(startOfDay, endOfDay, sectorName)).thenReturn(sessoes.filter { it.horarioSaida?.isAfter(startOfDay) == true && it.horarioSaida?.isBefore(endOfDay) == true })

        val expectedResponse = RevenueResponse(amount = 35.0, currency = "BRL", timestamp = LocalDateTime.now().with(date.atStartOfDay()).withNano(0))
        val actualResponse = revenueService.getRevenue(date, sectorName)

        assertEquals(expectedResponse.amount, actualResponse.amount)
        assertEquals(expectedResponse.currency, actualResponse.currency)
        assertEquals(expectedResponse.timestamp.toLocalDate(), actualResponse.timestamp.toLocalDate())
    }

    @Test
    fun `should return zero revenue when no sessions found for the date and sector`() {
        val date = LocalDate.of(2025, 5, 28)
        val sectorName = "B"
        val startOfDay = date.atStartOfDay()
        val endOfDay = date.plusDays(1).atStartOfDay().minusNanos(1)

        `when`(sessaoRepository.findAllByHorarioSaidaBetweenAndSetorNome(startOfDay, endOfDay, sectorName)).thenReturn(emptyList())

        val expectedResponse = RevenueResponse(amount = 0.0, currency = "BRL", timestamp = LocalDateTime.now().with(date.atStartOfDay()).withNano(0))
        val actualResponse = revenueService.getRevenue(date, sectorName)

        assertEquals(expectedResponse.amount, actualResponse.amount)
        assertEquals(expectedResponse.currency, actualResponse.currency)
    }
}