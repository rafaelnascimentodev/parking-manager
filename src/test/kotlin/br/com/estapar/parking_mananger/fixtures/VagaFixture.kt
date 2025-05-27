package br.com.estapar.parking_mananger.fixtures

import br.com.estapar.parking_mananger.model.Setor
import br.com.estapar.parking_mananger.model.Vaga

object VagaFixture {

    fun build(): Vaga {
        val setor = SetorFixture.build()
        return Vaga(
            id = 1L,
            setor = setor,
            latitude = -23.55052,
            longitude = -46.633308,
            ocupada = false
        )
    }
}