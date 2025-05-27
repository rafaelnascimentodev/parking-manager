package br.com.estapar.parking_mananger.controller

import br.com.estapar.parking_mananger.dto.RevenueResponse
import br.com.estapar.parking_mananger.service.RevenueService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping("/revenue")
@Tag(name = "Consulta de Faturamento", description = "Endpoint para consultar o faturamento por data e setor.")
class RevenueController(
    private val revenueService: RevenueService
) {

    @GetMapping
    @Operation(
        summary = "Consultar faturamento por data e setor",
        description = "Retorna o faturamento total para uma data e setor específicos.",
        responses = [
            ApiResponse(responseCode = "200", description = "Faturamento encontrado.", content = [Content(schema = Schema(implementation = RevenueResponse::class))]),
            ApiResponse(responseCode = "400", description = "Requisição inválida.", content = [Content(schema = Schema(implementation = String::class))]),
            ApiResponse(responseCode = "404", description = "Nenhum faturamento encontrado para os critérios informados.", content = [Content(schema = Schema(implementation = String::class))])
        ]
    )
    fun getRevenue(
        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate,
        @RequestParam("sector") sector: String
    ): ResponseEntity<RevenueResponse> {
        return try {
            val revenue = revenueService.getRevenue(date, sector)
            ResponseEntity.ok(revenue)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(RevenueResponse(0.0, "BRL", LocalDateTime.now()))
        }
    }
}