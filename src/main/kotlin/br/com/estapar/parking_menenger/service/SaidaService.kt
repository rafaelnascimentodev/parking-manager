package br.com.estapar.parking_menenger.service

import br.com.estapar.parking_menenger.config.StartupFlag
import br.com.estapar.parking_menenger.dto.SaidaRequest
import br.com.estapar.parking_menenger.repository.SessaoEstacionamentoRepository
import br.com.estapar.parking_menenger.repository.SetorRepository
import br.com.estapar.parking_menenger.repository.VagaRepository
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
        val duracao = Duration.between(sessao.horarioEntrada, sessao.horarioSaida)
        log.debug("Sessão encontrada: entrada=${sessao.horarioEntrada}, saída=${sessao.horarioSaida}, duração=${duracao.toMinutes()} minutos")

        val setor = setorRepository.findById(sessao.setor.id)
            .orElseThrow {
                log.warn("Setor com id=${sessao.setor.id} não encontrado")
                IllegalArgumentException("Setor não encontrado")
            }

        val preco = setor.precoBase * (duracao.toMinutes().toDouble() / 60.0)
        sessao.precoCobrado = preco
        sessao.estaAtiva = false
        sessaoRepository.save(sessao)
        log.info("Sessão encerrada para placa=${saidaRequest.placaVeiculo}, valor cobrado=R$%.2f".format(preco))

        val vaga = sessao.vaga
        vaga.ocupada = false
        vagaRepository.save(vaga)
        log.debug("Vaga ${vaga.id} marcada como disponível")
    }

}