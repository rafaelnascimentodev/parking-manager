package br.com.estapar.parking_menenger.controller

import br.com.estapar.parking_menenger.config.StartupFlag
import br.com.estapar.parking_menenger.dto.SaidaRequest
import br.com.estapar.parking_menenger.service.SaidaService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/saida-veiculo")
class SaidaController(
    private val saidaService: SaidaService,
    private val startupFlag: StartupFlag
) {

    @PostMapping
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