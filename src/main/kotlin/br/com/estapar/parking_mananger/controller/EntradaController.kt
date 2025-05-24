package br.com.estapar.parking_mananger.controller

import br.com.estapar.parking_mananger.config.StartupFlag
import br.com.estapar.parking_mananger.dto.EntradaRequest
import br.com.estapar.parking_mananger.service.EntradaService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/entrada-veiculo")
class EntradaController(
    private val entradaService: EntradaService,
    private val startupFlag: StartupFlag
) {

    @PostMapping
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

