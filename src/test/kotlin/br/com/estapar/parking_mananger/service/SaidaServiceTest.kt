package br.com.estapar.parking_mananger.service

import br.com.estapar.parking_mananger.config.StartupFlag
import br.com.estapar.parking_mananger.dto.SaidaRequest
import br.com.estapar.parking_mananger.fixtures.SessaoEstacionamentoFixture
import br.com.estapar.parking_mananger.model.SessaoEstacionamento
import br.com.estapar.parking_mananger.repository.SessaoEstacionamentoRepository
import br.com.estapar.parking_mananger.repository.SetorRepository
import br.com.estapar.parking_mananger.repository.VagaRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import org.mockito.kotlin.argumentCaptor
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class SaidaServiceTest {

    private lateinit var saidaService: SaidaService
    private lateinit var vagaRepository: VagaRepository
    private lateinit var sessaoRepository: SessaoEstacionamentoRepository
    private lateinit var startupFlag: StartupFlag

    @BeforeEach
    fun setup() {
        vagaRepository = mock(VagaRepository::class.java)
        sessaoRepository = mock(SessaoEstacionamentoRepository::class.java)
        startupFlag = StartupFlag()
        saidaService = SaidaService(vagaRepository, sessaoRepository, startupFlag)
    }

    @Test
    fun `registrarSaida deve encerrar sessão e liberar vaga com sucesso`() {
        startupFlag.sistemaPronto = true

        val sessao = SessaoEstacionamentoFixture.build()
        val request = SaidaRequest(sessao.veiculo.placa)

        `when`(sessaoRepository.findByVeiculoPlacaAndEstaAtivaTrue(sessao.veiculo.placa))
            .thenReturn(sessao)

        saidaService.registrarSaida(request)

        val sessaoCaptor = argumentCaptor<SessaoEstacionamento>()
        verify(sessaoRepository).save(sessaoCaptor.capture())

        val savedSessao = sessaoCaptor.firstValue
        assertNotNull(savedSessao.horarioSaida)
        assertFalse(savedSessao.estaAtiva)
        assertNotNull(savedSessao.precoCobrado)
        assertEquals(sessao.vaga.id, savedSessao.vaga.id)

        verify(vagaRepository).save(sessao.vaga)
        assertFalse(sessao.vaga.ocupada)
    }

    @Test
    fun `registrarSaida deve lançar exceção se sistema não estiver pronto`() {
        startupFlag.sistemaPronto = false
        val request = SaidaRequest("ABC-1234")

        assertThrows<IllegalStateException> {
            saidaService.registrarSaida(request)
        }

        verifyNoInteractions(sessaoRepository)
        verifyNoInteractions(vagaRepository)
    }

    @Test
    fun `registrarSaida deve lançar exceção se sessão não for encontrada`() {
        startupFlag.sistemaPronto = true
        val request = SaidaRequest("XYZ-9876")

        `when`(sessaoRepository.findByVeiculoPlacaAndEstaAtivaTrue(request.placaVeiculo))
            .thenReturn(null)

        assertThrows<IllegalArgumentException> {
            saidaService.registrarSaida(request)
        }

        verify(sessaoRepository).findByVeiculoPlacaAndEstaAtivaTrue(request.placaVeiculo)
        verifyNoInteractions(vagaRepository)

        verifyNoInteractions(vagaRepository)
    }
}
