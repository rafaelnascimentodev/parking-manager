package br.com.estapar.parking_mananger.fixtures

import br.com.estapar.parking_mananger.dto.WebhookRequest
import java.time.Instant

object WebhookRequestFixture {

    fun build(): WebhookRequest {
        return WebhookRequest(
            placaVeiculo = "ABC1234",
            horarioEntrada = Instant.parse("2025-01-01T12:00:00Z").toString(),
            horarioSaida = null,
            tipoEvento = "ENTRY",
            latitude = -23.55052,
            longitude = -46.633308
        )
    }

}