package com.example.frota.avaliacao;

import com.example.frota.solicitacaoTransporte.SolicitacaoTransporte;
import com.example.frota.solicitacaoTransporte.SolicitacaoTransporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service para gerenciar avaliações de entregas
 */
@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository repository;

    @Autowired
    private SolicitacaoTransporteRepository solicitacaoRepository;

    @Transactional
    public Avaliacao criarAvaliacao(Long solicitacaoId) {
        SolicitacaoTransporte solicitacao = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));

        // Verificar se já existe avaliação
        Optional<Avaliacao> avaliacaoExistente = repository.findBySolicitacaoId(solicitacaoId);
        if (avaliacaoExistente.isPresent()) {
            return avaliacaoExistente.get();
        }

        // Verificar se a entrega foi finalizada
        if (!solicitacao.estaFinalizada()) {
            throw new IllegalStateException("Solicitação ainda não foi finalizada");
        }

        Avaliacao avaliacao = new Avaliacao(solicitacao);
        return repository.save(avaliacao);
    }

    @Transactional
    public Avaliacao avaliarPorCliente(DadosAvaliacaoCliente dados) {
        Avaliacao avaliacao = repository.findBySolicitacaoId(dados.solicitacaoId())
                .orElseGet(() -> criarAvaliacao(dados.solicitacaoId()));

        avaliacao.avaliarPorCliente(dados.nota(), dados.comentario());
        return repository.save(avaliacao);
    }

    @Transactional
    public Avaliacao avaliarPorRecebedor(DadosAvaliacaoRecebedor dados) {
        Avaliacao avaliacao = repository.findBySolicitacaoId(dados.solicitacaoId())
                .orElseGet(() -> criarAvaliacao(dados.solicitacaoId()));

        avaliacao.avaliarPorRecebedor(dados.nota(), dados.comentario());
        return repository.save(avaliacao);
    }

    public List<DadosListagemAvaliacao> listarTodas() {
        return repository.findAll().stream()
                .map(DadosListagemAvaliacao::new)
                .collect(Collectors.toList());
    }

    public List<DadosListagemAvaliacao> listarCompletas() {
        return repository.findAvaliacoesCompletas().stream()
                .map(DadosListagemAvaliacao::new)
                .collect(Collectors.toList());
    }

    public List<DadosListagemAvaliacao> listarPendentesCliente() {
        return repository.findAvaliacoesPendentesCliente().stream()
                .map(DadosListagemAvaliacao::new)
                .collect(Collectors.toList());
    }

    public List<DadosListagemAvaliacao> listarPendentesRecebedor() {
        return repository.findAvaliacoesPendentesRecebedor().stream()
                .map(DadosListagemAvaliacao::new)
                .collect(Collectors.toList());
    }

    public Avaliacao buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada"));
    }

    public Optional<DadosListagemAvaliacao> buscarPorSolicitacao(Long solicitacaoId) {
        return repository.findBySolicitacaoId(solicitacaoId)
                .map(DadosListagemAvaliacao::new);
    }

    /**
     * Calcula a média geral de todas as avaliações
     */
    public Double calcularMediaGeral() {
        Double media = repository.calcularMediaGeralAvaliacoes();
        return media != null ? media : 0.0;
    }

    @Transactional
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}

