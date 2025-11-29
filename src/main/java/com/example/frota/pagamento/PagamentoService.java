package com.example.frota.pagamento;

import com.example.frota.enums.StatusPagamento;
import com.example.frota.solicitacaoTransporte.SolicitacaoTransporte;
import com.example.frota.solicitacaoTransporte.SolicitacaoTransporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service para gerenciar pagamentos de solicitações
 */
@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository repository;

    @Autowired
    private SolicitacaoTransporteRepository solicitacaoRepository;

    @Transactional
    public Pagamento criar(DadosCriacaoPagamento dados) {
        SolicitacaoTransporte solicitacao = solicitacaoRepository.findById(dados.solicitacaoId())
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));

        // Verificar se já existe pagamento
        Optional<Pagamento> pagamentoExistente = repository.findBySolicitacaoId(dados.solicitacaoId());
        if (pagamentoExistente.isPresent()) {
            throw new IllegalStateException("Solicitação já possui pagamento");
        }

        Pagamento pagamento = new Pagamento(dados, solicitacao);
        return repository.save(pagamento);
    }

    @Transactional
    public Pagamento confirmar(Long pagamentoId, String transacaoId) {
        Pagamento pagamento = buscarPorId(pagamentoId);
        pagamento.confirmar(transacaoId);
        return repository.save(pagamento);
    }

    @Transactional
    public Pagamento cancelar(Long pagamentoId, String motivo) {
        Pagamento pagamento = buscarPorId(pagamentoId);
        pagamento.cancelar(motivo);
        return repository.save(pagamento);
    }

    @Transactional
    public Pagamento processar(Long pagamentoId) {
        Pagamento pagamento = buscarPorId(pagamentoId);
        pagamento.processar();
        return repository.save(pagamento);
    }

    @Transactional
    public Pagamento reembolsar(Long pagamentoId, String motivo) {
        Pagamento pagamento = buscarPorId(pagamentoId);
        pagamento.reembolsar(motivo);
        return repository.save(pagamento);
    }

    public List<DadosListagemPagamento> listarTodos() {
        return repository.findAll().stream()
                .map(DadosListagemPagamento::new)
                .collect(Collectors.toList());
    }

    public List<DadosListagemPagamento> listarPendentes() {
        return repository.findPagamentosPendentes().stream()
                .map(DadosListagemPagamento::new)
                .collect(Collectors.toList());
    }

    public List<DadosListagemPagamento> listarPorStatus(StatusPagamento status) {
        return repository.findByStatus(status).stream()
                .map(DadosListagemPagamento::new)
                .collect(Collectors.toList());
    }

    public Pagamento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
    }

    public Optional<DadosListagemPagamento> buscarPorSolicitacao(Long solicitacaoId) {
        return repository.findBySolicitacaoId(solicitacaoId)
                .map(DadosListagemPagamento::new);
    }

    public Optional<Pagamento> buscarPorTransacaoId(String transacaoId) {
        return repository.findByTransacaoId(transacaoId);
    }

    /**
     * Calcula receita total no período
     */
    public Double calcularReceitaPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        Double receita = repository.calcularReceitaPeriodo(dataInicio, dataFim);
        return receita != null ? receita : 0.0;
    }

    /**
     * Cria pagamento automaticamente ao criar solicitação
     */
    @Transactional
    public Pagamento criarPagamentoAutomatico(SolicitacaoTransporte solicitacao, String metodoPagamento) {
        DadosCriacaoPagamento dados = new DadosCriacaoPagamento(
            solicitacao.getId(),
            solicitacao.getValorFrete(),
            metodoPagamento,
            "Pagamento criado automaticamente"
        );
        return criar(dados);
    }

    @Transactional
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}

