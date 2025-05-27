package br.com.estapar.parking_mananger.fixtures

import br.com.estapar.parking_mananger.model.Veiculo

object VeiculoFixture {

    fun build() = Veiculo(
        placa = "ABC-1234"
    )

}