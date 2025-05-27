package br.com.estapar.parking_mananger.fixtures

import br.com.estapar.parking_mananger.dto.EntradaRequest

object EntradaRequestFixtures {

    fun build(): EntradaRequest {
        return EntradaRequest(
            placa = "ABC1234",
            vagaId = 100L,
            setorId = 10L
        )
    }

}