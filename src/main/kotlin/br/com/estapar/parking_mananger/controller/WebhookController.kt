package br.com.estapar.parking_mananger.controller

import br.com.estapar.parking_mananger.dto.WebhookRequest
import br.com.estapar.parking_mananger.exception.BadRequestRuntimeException
import br.com.estapar.parking_mananger.service.WebhookService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/webhook")
@Tag(name = "Webhook", description = "Endpoint responsável por receber eventos externos (entrada/saída de veículos).")
class WebhookController(
    private val webhookService: WebhookService
) {
    private val log = LoggerFactory.getLogger(WebhookController::class.java)

    @PostMapping
    @Operation(
        summary = "Receber evento de webhook",
        description = "Recebe eventos externos (geralmente de entrada ou saída de veículos) e os processa internamente.",
        responses = [
            ApiResponse(responseCode = "200", description = "Evento processado com sucesso."),
            ApiResponse(responseCode = "400", description = "Requisição inválida.", content = [Content(schema = Schema(implementation = String::class))]),
            ApiResponse(responseCode = "500", description = "Erro interno ao processar o evento.", content = [Content(schema = Schema(implementation = String::class))])
        ]
    )
    fun receberEvento(@RequestBody request: WebhookRequest): ResponseEntity<String> {
        return try {
            webhookService.processarEvento(request)
            ResponseEntity.ok("Evento processado com sucesso")
        } catch (ex: BadRequestRuntimeException) {
            log.warn("Requisição inválida: ${ex.message}")
            ResponseEntity.badRequest().body(ex.message)
        } catch (ex: Exception) {
            log.error("Erro ao processar evento webhook", ex)
            ResponseEntity.internalServerError().body("Erro interno: ${ex.message}")
        }
    }
}
