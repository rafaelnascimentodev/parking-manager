package br.com.estapar.parking_mananger.service

import br.com.estapar.parking_mananger.config.StartupFlag
import br.com.estapar.parking_mananger.dto.SaidaRequest
import br.com.estapar.parking_mananger.repository.SessaoEstacionamentoRepository
import br.com.estapar.parking_mananger.repository.SetorRepository
import br.com.estapar.parking_mananger.repository.VagaRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

@Service
class SaidaService(
    private val vagaRepository: VagaRepository,
    private val sessaoRepository: SessaoEstacionamentoRepository,
    private val setorRepository: SetorRepository,
    private val startupFlag: StartupFlag
) {
    private val log: Logger = LoggerFactory.getLogger(SaidaService::class.java)

    fun registrarSaida(saidaRequest: SaidaRequest) {
        log.info("Registrando saída para placa=${saidaRequest.placaVeiculo}")

        if (!startupFlag.sistemaPronto) {
            log.error("Tentativa de registrar saída antes da inicialização do sistema")
            throw IllegalStateException("Sistema ainda não inicializado com os dados da garagem.")
        }

        val sessao = sessaoRepository.findByVeiculoPlacaAndEstaAtivaTrue(saidaRequest.placaVeiculo)
            ?: run {
                log.warn("Sessão ativa não encontrada para o veículo com placa=${saidaRequest.placaVeiculo}")
                throw IllegalArgumentException("Sessão ativa não encontrada para o veículo.")
            }

        sessao.horarioSaida = LocalDateTime.now()

        val duracaoMinutos = Duration.between(sessao.horarioEntrada, sessao.horarioSaida).toMinutes()
        log.debug("Sessão encontrada: entrada=${sessao.horarioEntrada}, saída=${sessao.horarioSaida}, duração=${duracaoMinutos} minutos")

        // Calcula o preço conforme regra
        val preco = calcularPrecoComRegra(sessao.precoBase, duracaoMinutos)
        sessao.precoCobrado = preco
        sessao.estaAtiva = false
        sessaoRepository.save(sessao)
        log.info("Sessão encerrada para placa=${saidaRequest.placaVeiculo}, valor cobrado=R$%.2f".format(preco))

        val vaga = sessao.vaga
        vaga.ocupada = false
        vagaRepository.save(vaga)
        log.debug("Vaga ${vaga.id} marcada como disponível")
    }

    private fun calcularPrecoComRegra(precoBase: Double, duracaoMinutos: Long): Double {
        if (duracaoMinutos <= 15) {
            // Carência de 15 minutos - sem cobrança
            return 0.0
        }

        // Primeira hora: 100% do preço base
        if (duracaoMinutos <= 60) {
            return precoBase
        }

        // Após a primeira hora, cobrar pró-rata em blocos de 15 minutos
        val minutosExtras = duracaoMinutos - 60
        val blocos15Minutos = Math.ceil(minutosExtras / 15.0).toInt()

        val precoExtras = precoBase * 0.25 * blocos15Minutos  // cada bloco de 15min = 25% do preço base (1h)

        return precoBase + precoExtras
    }
}
