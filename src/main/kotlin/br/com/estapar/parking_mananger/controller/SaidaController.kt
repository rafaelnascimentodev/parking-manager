package br.com.estapar.parking_mananger.controller

import br.com.estapar.parking_mananger.config.StartupFlag
import br.com.estapar.parking_mananger.dto.SaidaRequest
import br.com.estapar.parking_mananger.service.SaidaService
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
@RequestMapping("/saida-veiculo")
@Tag(name = "Saída de Veículo", description = "Endpoint para registrar a saída de veículos.")
class SaidaController(
    private val saidaService: SaidaService,
    private val startupFlag: StartupFlag
) {

    @PostMapping
    @Operation(
        summary = "Registrar saída de veículo",
        description = "Registra a saída de um veículo do estacionamento e retorna o valor cobrado.",
        responses = [
            ApiResponse(responseCode = "200", description = "Saída registrada com sucesso.", content = [Content(schema = Schema(implementation = String::class))]),
            ApiResponse(responseCode = "400", description = "Erro na requisição (ex: veículo não encontrado).", content = [Content(schema = Schema(implementation = String::class))]),
            ApiResponse(responseCode = "503", description = "Sistema ainda não inicializado com os dados da garagem.", content = [Content(schema = Schema(implementation = String::class))])
        ]
    )
    fun registrarSaida(@RequestBody saidaRequest: SaidaRequest): ResponseEntity<String> {
        if (!startupFlag.sistemaPronto) {
            return ResponseEntity.status(503).body("Sistema ainda não inicializado com os dados da garagem.")
        }

        return try {
            val valorCobrado = saidaService.registrarSaida(saidaRequest)
            ResponseEntity.ok("Saída registrada com sucesso. Valor cobrado: $valorCobrado")
        } catch (ex: Exception) {
            ResponseEntity.badRequest().body("Erro ao registrar saída: ${ex.message}")
        }
    }

}