package br.com.estapar.parking_mananger.controller

import br.com.estapar.parking_mananger.config.StartupFlag
import br.com.estapar.parking_mananger.fixtures.EntradaRequestFixtures
import br.com.estapar.parking_mananger.service.EntradaService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class EntradaControllerIntegrationTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper
    private val entradaService = mock<EntradaService>()
    private val startupFlag = mock<StartupFlag>()
    private val controller = EntradaController(entradaService, startupFlag)

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        objectMapper = ObjectMapper()
    }

    @Test
    fun `deve retornar 503 se sistema nao estiver pronto`() {
        `when`(startupFlag.sistemaPronto).thenReturn(false)

        val request = EntradaRequestFixtures.build()

        mockMvc.perform(
            post("/entrada-veiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isServiceUnavailable)
            .andExpect(content().string("Sistema ainda não inicializado com os dados da garagem."))
    }

    @Test
    fun `deve registrar entrada com sucesso`() {
        `when`(startupFlag.sistemaPronto).thenReturn(true)

        val request = EntradaRequestFixtures.build()

        doNothing().`when`(entradaService).registrarEntrada(request)

        mockMvc.perform(
            post("/entrada-veiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)
            .andExpect(content().string("Entrada registrada com sucesso."))
    }

    @Test
    fun `deve retornar erro 400 se houver excecao na service`() {
        `when`(startupFlag.sistemaPronto).thenReturn(true)

        val request = EntradaRequestFixtures.build()

        doThrow(RuntimeException("Setor inexistente")).`when`(entradaService).registrarEntrada(request)

        mockMvc.perform(
            post("/entrada-veiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest)
            .andExpect(content().string("Erro ao registrar entrada: Setor inexistente"))
    }

}