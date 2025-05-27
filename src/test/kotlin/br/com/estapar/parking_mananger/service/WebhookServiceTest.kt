package br.com.estapar.parking_mananger.service

import br.com.estapar.parking_mananger.fixtures.SetorFixture
import br.com.estapar.parking_mananger.fixtures.VagaFixture
import br.com.estapar.parking_mananger.fixtures.WebhookRequestFixture
import br.com.estapar.parking_mananger.repository.SetorRepository
import br.com.estapar.parking_mananger.repository.VagaRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any

@ExtendWith(MockitoExtension::class)
class WebhookServiceTest {

    @Mock
    lateinit var vagaRepository: VagaRepository
    @Mock
    lateinit var entradaService: EntradaService
    @Mock
    lateinit var saidaService: SaidaService
    @Mock
    lateinit var setorRepository: SetorRepository

    @InjectMocks
    lateinit var service: WebhookService

    @Test
    fun `deve chamar registrarEntrada quando event_type for ENTRY e vaga existir`() {
        val request = WebhookRequestFixture.build()
        val vaga = VagaFixture.build()
        val setor = SetorFixture.build()

        `when`(vagaRepository.findByLatitudeAndLongitude(any(), any())).thenReturn(vaga)
        doNothing().`when`(entradaService).registrarEntrada(any())

        `when`(setorRepository.findById(setor.id)).thenReturn(java.util.Optional.of(setor))
        service.processarEvento(request)

        verify(entradaService, times(1)).registrarEntrada(any())
        verify(saidaService, never()).registrarSaida(any())
    }

    @Test
    fun `deve lançar erro quando event_type for ENTRY e vaga for obrigatória mas não encontrada`() {
        val request = WebhookRequestFixture.build()

        `when`(vagaRepository.findByLatitudeAndLongitude(request.latitude!!, request.longitude!!)).thenReturn(null)

        assertThrows<org.apache.coyote.BadRequestException> {
            service.processarEvento(request)
        }
    }

    @Test
    fun `deve chamar registrarSaida quando event_type for EXIT`() {
        val request = WebhookRequestFixture.build().copy(
            tipoEvento = "EXIT",
            latitude = -23.55052,
            longitude = -46.633308
        )

        doNothing().`when`(saidaService).registrarSaida(any())

        service.processarEvento(request)

        verify(saidaService, times(1)).registrarSaida(any())
        verify(entradaService, never()).registrarEntrada(any())
    }

}
