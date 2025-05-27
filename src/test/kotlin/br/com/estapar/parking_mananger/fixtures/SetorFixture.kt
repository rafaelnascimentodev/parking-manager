package br.com.estapar.parking_mananger.fixtures

import br.com.estapar.parking_mananger.model.Setor
import java.time.LocalTime

object SetorFixture {

    fun build() = Setor(
        id = 1L,
        nome = "Setor A",
        precoBase = 10.0,
        capacidadeMaxima = 100,
        horarioAbertura = LocalTime.of(8, 0),
        horarioFechamento = LocalTime.of(20, 0),
        limiteDuracaoMinutos = 120
    )

}