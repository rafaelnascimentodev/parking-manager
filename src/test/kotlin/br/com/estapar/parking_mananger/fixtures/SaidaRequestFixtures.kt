package br.com.estapar.parking_mananger.fixtures

import br.com.estapar.parking_mananger.dto.SaidaRequest

object SaidaRequestFixtures {

    fun build(): SaidaRequest {
        return SaidaRequest(
            placaVeiculo = "ABC1234"
        )
    }

}