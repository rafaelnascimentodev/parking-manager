package br.com.estapar.parking_mananger.controller

import br.com.estapar.parking_mananger.config.StartupFlag
import br.com.estapar.parking_mananger.dto.EntradaRequest
import br.com.estapar.parking_mananger.service.EntradaService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/entrada-veiculo")
@Tag(name = "Entrada de Veículo", description = "Endpoint para registrar a entrada de veículos no estacionamento.")
class EntradaController(
    private val entradaService: EntradaService,
    private val startupFlag: StartupFlag
) {

    @PostMapping
    @Operation(
        summary = "Registrar entrada de veículo",
        description = "Registra a entrada de um veículo em uma vaga de um setor específico.",
        responses = [
            ApiResponse(responseCode = "200", description = "Entrada registrada com sucesso."),
            ApiResponse(responseCode = "400", description = "Erro de validação ou processamento.", content = [Content(schema = Schema(implementation = String::class))]),
            ApiResponse(responseCode = "503", description = "Sistema ainda não está pronto para receber entradas.", content = [Content(schema = Schema(implementation = String::class))])
        ]
    )
    fun registrarEntrada(@RequestBody entradaRequest: EntradaRequest): ResponseEntity<String> {
        if (!startupFlag.sistemaPronto) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Sistema ainda não inicializado com os dados da garagem.")
        }

        return try {
            entradaService.registrarEntrada(entradaRequest)
            ResponseEntity.ok("Entrada registrada com sucesso.")
        } catch (ex: Exception) {
            ResponseEntity.badRequest().body("Erro ao registrar entrada: ${ex.message}")
        }
    }

}

