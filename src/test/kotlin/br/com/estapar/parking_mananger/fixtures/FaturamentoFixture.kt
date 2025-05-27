package br.com.estapar.parking_mananger.fixtures

import br.com.estapar.parking_mananger.model.Faturamento
import java.time.LocalDate

object FaturamentoFixture {

    fun build() = Faturamento(
        id = "faturamento-2025-05-26",
        setor = "Setor A",
        data = LocalDate.now(),
        valor = 250.0
    )

}
