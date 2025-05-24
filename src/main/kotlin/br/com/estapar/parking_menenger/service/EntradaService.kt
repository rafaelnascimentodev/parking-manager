package br.com.estapar.parking_menenger.service

import br.com.estapar.parking_menenger.config.StartupFlag
import br.com.estapar.parking_menenger.dto.EntradaRequest
import br.com.estapar.parking_menenger.exception.SetorNotFoundEsception
import br.com.estapar.parking_menenger.exception.VagaNotFoundEsception
import br.com.estapar.parking_menenger.model.SessaoEstacionamento
import br.com.estapar.parking_menenger.model.Veiculo
import br.com.estapar.parking_menenger.repository.SessaoEstacionamentoRepository
import br.com.estapar.parking_menenger.repository.SetorRepository
import br.com.estapar.parking_menenger.repository.VagaRepository
import br.com.estapar.parking_menenger.repository.VeiculoRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class EntradaService(
    private val veiculoRepository: VeiculoRepository,
    private val vagaRepository: VagaRepository,
    private val sessaoRepository: SessaoEstacionamentoRepository,
    private val startupFlag: StartupFlag,
    private val setorRepository: SetorRepository
) {
    private val log: Logger = LoggerFactory.getLogger(EntradaService::class.java)

    fun registrarEntrada(entradaRequest: EntradaRequest) {
        log.info("Registrando entrada para placa=${entradaRequest.placa} na vaga=${entradaRequest.vagaId} no setor=${entradaRequest.setorId}")

        if (!startupFlag.sistemaPronto) {
            log.error("Tentativa de registrar entrada antes da inicialização do sistema")
            throw IllegalStateException("Sistema ainda não inicializado com os dados da garagem.")
        }

        val veiculo = veiculoRepository.findById(entradaRequest.placa).orElseGet {
            log.info("Veículo com placa=${entradaRequest.placa} não encontrado. Criando novo registro.")
            veiculoRepository.save(Veiculo(entradaRequest.placa))
        }

        val vaga = vagaRepository.findById(entradaRequest.vagaId)
            .orElseThrow {
                log.warn("Vaga com id=${entradaRequest.vagaId} não encontrada")
                VagaNotFoundEsception("Vaga não encontrada") }
        vaga.ocupada = true
        vagaRepository.save(vaga)
        log.debug("Vaga ${entradaRequest.vagaId} marcada como ocupada")

        val setor = setorRepository.findById(entradaRequest.setorId)
            .orElseThrow{ SetorNotFoundEsception("Setor não encontrado") }

        val sessao = SessaoEstacionamento(
            veiculo = veiculo,
            vaga = vaga,
            setor = setor,
            horarioEntrada = LocalDateTime.now(),
            estaAtiva = true
        )
        sessaoRepository.save(sessao)
        log.info("Sessão de estacionamento registrada para placa=${entradaRequest.placa} na vaga=${entradaRequest.vagaId}")
    }

}