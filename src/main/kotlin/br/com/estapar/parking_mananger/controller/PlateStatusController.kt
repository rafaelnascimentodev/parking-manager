package br.com.estapar.parking_mananger.controller

import br.com.estapar.parking_mananger.dto.PlateStatusResponse
import br.com.estapar.parking_mananger.service.PlateStatusService
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class PlateStatusRequest(
    @JsonProperty("license_plate") val licensePlate: String
)

@RestController
@RequestMapping("/plate-status")
@Tag(name = "Consulta de Placa", description = "Endpoint para consultar o status de um veículo pela placa.")
class PlateStatusController(
    private val plateStatusService: PlateStatusService
) {

    @PostMapping
    @Operation(
        summary = "Consultar status do veículo por placa",
        description = "Retorna o status atual de um veículo estacionado (preço atual, hora de entrada, tempo estacionado).",
        responses = [
            ApiResponse(responseCode = "200", description = "Status do veículo encontrado.", content = [Content(schema = Schema(implementation = PlateStatusResponse::class))]),
            ApiResponse(responseCode = "404", description = "Nenhuma sessão ativa encontrada para a placa informada.", content = [Content(schema = Schema(implementation = String::class))]),
            ApiResponse(responseCode = "400", description = "Requisição inválida.", content = [Content(schema = Schema(implementation = String::class))])
        ]
    )
    fun getPlateStatus(@RequestBody request: PlateStatusRequest): ResponseEntity<PlateStatusResponse> {
        val status = plateStatusService.getPlateStatus(request.licensePlate)
        return status?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }
}