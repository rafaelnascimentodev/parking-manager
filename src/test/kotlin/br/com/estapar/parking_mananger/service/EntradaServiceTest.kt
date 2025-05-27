package br.com.estapar.parking_mananger.service

import br.com.estapar.parking_mananger.config.StartupFlag
import br.com.estapar.parking_mananger.dto.EntradaRequest
import br.com.estapar.parking_mananger.fixtures.SetorFixture
import br.com.estapar.parking_mananger.fixtures.VagaFixture
import br.com.estapar.parking_mananger.fixtures.VeiculoFixture
import br.com.estapar.parking_mananger.model.SessaoEstacionamento
import br.com.estapar.parking_mananger.repository.SessaoEstacionamentoRepository
import br.com.estapar.parking_mananger.repository.SetorRepository
import br.com.estapar.parking_mananger.repository.VagaRepository
import br.com.estapar.parking_mananger.repository.VeiculoRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import org.mockito.kotlin.argumentCaptor
import java.util.*

class EntradaServiceTest {

    private lateinit var entradaService: EntradaService
    private lateinit var veiculoRepository: VeiculoRepository
    private lateinit var vagaRepository: VagaRepository
    private lateinit var sessaoRepository: SessaoEstacionamentoRepository
    private lateinit var startupFlag: StartupFlag
    private lateinit var setorRepository: SetorRepository

    @BeforeEach
    fun setup() {
        veiculoRepository = mock(VeiculoRepository::class.java)
        vagaRepository = mock(VagaRepository::class.java)
        sessaoRepository = mock(SessaoEstacionamentoRepository::class.java)
        setorRepository = mock(SetorRepository::class.java)
        startupFlag = StartupFlag()
        entradaService = EntradaService(veiculoRepository, vagaRepository, sessaoRepository, startupFlag, setorRepository)
    }

    @Test
    fun `testRegistrarEntrada deve registrar com sucesso`() {
        startupFlag.sistemaPronto = true
        val setor = SetorFixture.build()
        val vaga = VagaFixture.build()
        val veiculo = VeiculoFixture.build()
        val request = EntradaRequest(veiculo.placa, vaga.id, setor.id)

        `when`(setorRepository.findById(setor.id!!)).thenReturn(Optional.of(setor))
        `when`(vagaRepository.countBySetorId(setor.id!!)).thenReturn(10)
        `when`(vagaRepository.countBySetorIdAndOcupadaTrue(setor.id!!)).thenReturn(5)
        `when`(veiculoRepository.findById(veiculo.placa)).thenReturn(Optional.of(veiculo))
        `when`(vagaRepository.findById(vaga.id!!)).thenReturn(Optional.of(vaga))

        entradaService.registrarEntrada(request)

        verify(vagaRepository).save(vaga)

        val captor = argumentCaptor<SessaoEstacionamento>()
        verify(sessaoRepository).save(captor.capture())

        val saved = captor.firstValue
        assertEquals(veiculo.placa, saved.veiculo.placa)
        assertEquals(setor.id, saved.setor.id)
        assertEquals(vaga.id, saved.vaga.id)
        assertTrue(saved.estaAtiva)
        assertEquals(setor.precoBase, saved.precoBase)
        assertNotNull(saved.horarioEntrada)
    }


    @Test
    fun `testRegistrarEntrada deve lançar exceção se setor estiver lotado`() {
        startupFlag.sistemaPronto = true
        var setor = SetorFixture.build();
        var vaga = VagaFixture.build();
        var veiculo = VeiculoFixture.build();
        var request = EntradaRequest(veiculo.placa, vaga.id, setor.id);

        `when`(setorRepository.findById(setor.id!!)).thenReturn(Optional.of(setor))
        `when`(vagaRepository.countBySetorId(setor.id!!)).thenReturn(10)
        `when`(vagaRepository.countBySetorIdAndOcupadaTrue(setor.id!!)).thenReturn(10)

        assertThrows<IllegalStateException> {
            entradaService.registrarEntrada(request)
        }

        verifyNoInteractions(veiculoRepository)
        verifyNoInteractions(sessaoRepository)
    }
}
