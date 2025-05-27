package br.com.estapar.parking_mananger.controller

import br.com.estapar.parking_mananger.service.WebhookService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.times
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(WebhookController::class)
class WebhookControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @TestConfiguration
    class TestConfig {
        @Bean
        fun webhookService(): WebhookService = mock(WebhookService::class.java)
    }

    @Autowired
    private lateinit var webhookService: WebhookService

    @Test
    fun `deve aceitar evento ENTRY e retornar 200`() {
        val json = """
        {
          "license_plate": "ZUL0001",
          "entry_time": "2025-01-01T12:00:00.000Z",
          "event_type": "ENTRY"
        }
        """

        doNothing().`when`(webhookService).processarEvento(any())

        mockMvc.perform(
            post("/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("Evento processado com sucesso"))

        verify(webhookService, times(1)).processarEvento(any())
    }
}