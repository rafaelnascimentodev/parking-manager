package br.com.estapar.parking_mananger.controller

import br.com.estapar.parking_mananger.dto.SpotStatusRequest
import br.com.estapar.parking_mananger.dto.SpotStatusResponse
import br.com.estapar.parking_mananger.service.SpotStatusService
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

@RestController
@RequestMapping("/spot-status")
@Tag(name = "Consulta de Vaga", description = "Endpoint para consultar o status de uma vaga por latitude e longitude.")
class SpotStatusController(
    private val spotStatusService: SpotStatusService
) {

    @PostMapping
    @Operation(
        summary = "Consultar status da vaga por coordenadas",
        description = "Retorna o status (ocupada ou não, hora de entrada, tempo estacionado) de uma vaga.",
        responses = [
            ApiResponse(responseCode = "200", description = "Status da vaga encontrado.", content = [Content(schema = Schema(implementation = SpotStatusResponse::class))]),
            ApiResponse(responseCode = "404", description = "Nenhuma vaga encontrada para as coordenadas informadas.", content = [Content(schema = Schema(implementation = String::class))]),
            ApiResponse(responseCode = "400", description = "Requisição inválida.", content = [Content(schema = Schema(implementation = String::class))])
        ]
    )
    fun getSpotStatus(@RequestBody request: SpotStatusRequest): ResponseEntity<SpotStatusResponse> {
        val status = spotStatusService.getSpotStatus(request.latitude, request.longitude)
        return status?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }
}