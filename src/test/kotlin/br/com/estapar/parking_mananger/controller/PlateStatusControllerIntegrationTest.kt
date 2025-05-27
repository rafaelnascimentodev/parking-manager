package br.com.estapar.parking_mananger.controller

import br.com.estapar.parking_mananger.dto.PlateStatusResponse
import br.com.estapar.parking_mananger.service.PlateStatusService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@WebMvcTest(PlateStatusController::class)
class PlateStatusControllerMockitoIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @TestConfiguration
    class TestConfig {
        @Bean
        fun plateStatusService(): PlateStatusService = org.mockito.Mockito.mock(PlateStatusService::class.java)
    }

    @Autowired
    private lateinit var plateStatusService: PlateStatusService

    @Test
    fun `should return ok and plate status when valid request`() {
        val licensePlate = "ABC1234"
        val now = LocalDateTime.now()
        val entryTime = now.minusHours(1)
        val price = 10.0
        val timeParked = "01h 00m"
        val response = PlateStatusResponse(licensePlate, price, entryTime, timeParked)

        `when`(plateStatusService.getPlateStatus(licensePlate)).thenReturn(response)

        mockMvc.post("/plate-status") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(mapOf("license_plate" to licensePlate))
        }.andExpect {
            status().isOk
            jsonPath("$.license_plate").value(licensePlate)
            jsonPath("$.price_until_now").value(price)
            jsonPath("$.entry_time").value(entryTime.format(DateTimeFormatter.ISO_DATE_TIME))
            jsonPath("$.time_parked").value(timeParked)
        }
    }

    @Test
    fun `should return not found when no status for plate`() {
        val licensePlate = "XYZ9876"
        `when`(plateStatusService.getPlateStatus(licensePlate)).thenReturn(null)

        mockMvc.post("/plate-status") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(mapOf("license_plate" to licensePlate))
        }.andExpect {
            status().isNotFound
        }
    }

    @Test
    fun `should return bad request when license plate is missing`() {
        mockMvc.post("/plate-status") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(emptyMap<String, String>())
        }.andExpect {
            status().isBadRequest // Assuming validation will handle this
        }
    }
}