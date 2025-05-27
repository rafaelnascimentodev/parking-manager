package br.com.estapar.parking_mananger.fixtures

import br.com.estapar.parking_mananger.model.SessaoEstacionamento
import br.com.estapar.parking_mananger.model.Setor
import br.com.estapar.parking_mananger.model.Vaga
import br.com.estapar.parking_mananger.model.Veiculo
import java.time.LocalDateTime

object SessaoEstacionamentoFixture {

    fun build(): SessaoEstacionamento {
        val veiculo = VeiculoFixture.build()
        val vaga = VagaFixture.build()
        val setor = vaga.setor

        return SessaoEstacionamento(
            id = 1L,
            veiculo = veiculo,
            vaga = vaga,
            setor = setor,
            horarioEntrada = LocalDateTime.now().minusMinutes(30),
            horarioSaida = null,
            estaAtiva = true,
            precoCobrado = null,
            precoBase = setor.precoBase
        )
    }

}
