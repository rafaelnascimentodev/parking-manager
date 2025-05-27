package br.com.estapar.parking_mananger.controller

import br.com.estapar.parking_mananger.dto.RevenueResponse
import br.com.estapar.parking_mananger.service.RevenueService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.time.LocalDateTime
import org.springframework.test.web.servlet.MvcResult
import org.junit.jupiter.api.Assertions.assertEquals

@WebMvcTest(RevenueController::class)
class RevenueControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @TestConfiguration
    class TestConfig {
        @Bean
        fun revenueService(): RevenueService = mock(RevenueService::class.java)
    }

    @Autowired
    private lateinit var revenueService: RevenueService

    @Test
    fun `should return ok and revenue amount for valid date and sector`() {
        val date = LocalDate.of(2025, 5, 27)
        val sector = "A"
        val amount = 50.0
        val timestamp = LocalDateTime.now()
        val expectedResponse = RevenueResponse(amount = amount, currency = "BRL", timestamp = timestamp)

        `when`(revenueService.getRevenue(date, sector)).thenReturn(expectedResponse)

        val result: MvcResult = mockMvc.get("/revenue?date={date}&sector={sector}", date, sector)
            .andExpect { status().isOk }
            .andReturn()

        val actualResponse = objectMapper.readValue(result.response.contentAsString, RevenueResponse::class.java)
        assertEquals(expectedResponse.copy(timestamp = expectedResponse.timestamp.withNano(0)), actualResponse.copy(timestamp = actualResponse.timestamp.withNano(0)))
    }

    @Test
    fun `should return ok with zero amount when no revenue for date and sector`() {
        val date = LocalDate.of(2025, 5, 28)
        val sector = "B"
        val expectedResponse = RevenueResponse(amount = 0.0, currency = "BRL", timestamp = LocalDateTime.now())

        `when`(revenueService.getRevenue(date, sector)).thenReturn(expectedResponse)

        val result: MvcResult = mockMvc.get("/revenue?date={date}&sector={sector}", date, sector)
            .andExpect { status().isOk }
            .andReturn()

        val actualResponse = objectMapper.readValue(result.response.contentAsString, RevenueResponse::class.java)
        assertEquals(expectedResponse.copy(timestamp = expectedResponse.timestamp.withNano(0)), actualResponse.copy(timestamp = actualResponse.timestamp.withNano(0)))
    }

    @Test
    fun `should return bad request when date is missing`() {
        val sector = "A"
        mockMvc.get("/revenue?sector={sector}", sector)
            .andExpect { status().isBadRequest }
    }

    @Test
    fun `should return bad request when sector is missing`() {
        val date = LocalDate.of(2025, 5, 27)
        mockMvc.get("/revenue?date={date}", date)
            .andExpect { status().isBadRequest }
    }

    @Test
    fun `should return bad request when both date and sector are missing`() {
        mockMvc.get("/revenue")
            .andExpect { status().isBadRequest }
    }
}