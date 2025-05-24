package br.com.estapar.parking_mananger.service

import br.com.estapar.parking_mananger.config.StartupFlag
import br.com.estapar.parking_mananger.dto.EntradaRequest
import br.com.estapar.parking_mananger.exception.SetorNotFoundEsception
import br.com.estapar.parking_mananger.exception.VagaNotFoundEsception
import br.com.estapar.parking_mananger.model.SessaoEstacionamento
import br.com.estapar.parking_mananger.model.Veiculo
import br.com.estapar.parking_mananger.repository.SessaoEstacionamentoRepository
import br.com.estapar.parking_mananger.repository.SetorRepository
import br.com.estapar.parking_mananger.repository.VagaRepository
import br.com.estapar.parking_mananger.repository.VeiculoRepository
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

        val setor = setorRepository.findById(entradaRequest.setorId)
            .orElseThrow {
                log.warn("Setor com id=${entradaRequest.setorId} não encontrado")
                throw SetorNotFoundEsception("Setor não encontrado")
            }

        // Verifica lotação do setor
        val totalVagas = vagaRepository.countBySetorId(setor.id!!)
        val vagasOcupadas = vagaRepository.countBySetorIdAndOcupadaTrue(setor.id)
        val lotacao = vagasOcupadas.toDouble() / totalVagas.toDouble()

        log.info("Lotação atual do setor '${setor.nome}': ${(lotacao * 100).toInt()}%")

        if (lotacao >= 1.0) {
            log.warn("Setor '${setor.nome}' está lotado. Entrada bloqueada.")
            throw IllegalStateException("Setor '${setor.nome}' está lotado. Não é possível registrar nova entrada.")
        }

        val veiculo = veiculoRepository.findById(entradaRequest.placa).orElseGet {
            log.info("Veículo com placa=${entradaRequest.placa} não encontrado. Criando novo registro.")
            veiculoRepository.save(Veiculo(entradaRequest.placa))
        }

        val vaga = vagaRepository.findById(entradaRequest.vagaId)
            .orElseThrow {
                log.warn("Vaga com id=${entradaRequest.vagaId} não encontrada")
                throw VagaNotFoundEsception("Vaga não encontrada")
            }

        // Marca a vaga como ocupada
        vaga.ocupada = true
        vagaRepository.save(vaga)
        log.debug("Vaga ${entradaRequest.vagaId} marcada como ocupada")

        // Calcula o preço base ajustado conforme regra de preço dinâmico
        val precoAjustado = calcularPrecoDinamico(setor.precoBase, lotacao)
        log.info("Preço base: R$${setor.precoBase} | Preço ajustado: R$${"%.2f".format(precoAjustado)}")

        // Cria sessão salvando preçoBase ajustado para usar na cobrança posterior
        val sessao = SessaoEstacionamento(
            veiculo = veiculo,
            vaga = vaga,
            setor = setor,
            horarioEntrada = LocalDateTime.now(),
            estaAtiva = true,
            precoBase = precoAjustado // novo campo na entidade SessaoEstacionamento
        )
        sessaoRepository.save(sessao)
        log.info("Sessão de estacionamento registrada para placa=${entradaRequest.placa} na vaga=${entradaRequest.vagaId}")
    }

    private fun calcularPrecoDinamico(precoBase: Double, lotacao: Double): Double {
        return when {
            lotacao < 0.25 -> precoBase * 0.9       // desconto 10%
            lotacao <= 0.5 -> precoBase             // 0% desconto
            lotacao <= 0.75 -> precoBase * 1.1      // aumenta 10%
            else -> precoBase * 1.25                 // aumenta 25%
        }
    }
}
