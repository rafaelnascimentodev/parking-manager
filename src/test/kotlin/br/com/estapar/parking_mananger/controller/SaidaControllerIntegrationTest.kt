package br.com.estapar.parking_mananger.controller

import br.com.estapar.parking_mananger.config.StartupFlag
import br.com.estapar.parking_mananger.fixtures.SaidaRequestFixtures
import br.com.estapar.parking_mananger.service.SaidaService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class SaidaControllerIntegrationTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper
    private val saidaService = mock(SaidaService::class.java)
    private val startupFlag = mock(StartupFlag::class.java)
    private val controller = SaidaController(saidaService, startupFlag)

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        objectMapper = ObjectMapper()
    }

    @Test
    fun `deve retornar 503 se sistema nao estiver pronto`() {
        `when`(startupFlag.sistemaPronto).thenReturn(false)

        val request = SaidaRequestFixtures.build()

        mockMvc.perform(
            post("/saida-veiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isServiceUnavailable)
            .andExpect(content().string("Sistema ainda não inicializado com os dados da garagem."))
    }

    @Test
    fun `deve registrar saida com sucesso e retornar valor cobrado`() {
        `when`(startupFlag.sistemaPronto).thenReturn(true)

        val request = SaidaRequestFixtures.build()

        doNothing().`when`(saidaService).registrarSaida(request)

        mockMvc.perform(
            post("/saida-veiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Saída registrada com sucesso")))
    }


    @Test
    fun `deve retornar erro 400 se houver excecao na service`() {
        `when`(startupFlag.sistemaPronto).thenReturn(true)

        val request = SaidaRequestFixtures.build()

        `when`(saidaService.registrarSaida(request)).thenThrow(RuntimeException("Veículo não encontrado"))

        mockMvc.perform(
            post("/saida-veiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest)
            .andExpect(content().string("Erro ao registrar saída: Veículo não encontrado"))
    }

}
