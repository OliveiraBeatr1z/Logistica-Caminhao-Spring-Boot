package com.example.frota.solicitacaoTransporte;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class SolicitacaoTransporteService {
    
    @Autowired
    private SolicitacaoTransporteRepository repository;

    @Autowired
    private FreightService freightService;

    public List<SolicitacaoTransporte> getAllSolicitacoes() {
        return repository.findAll(Sort.by("id").descending());
    }

    public SolicitacaoTransporte getSolicitacaoById(Long id) {
        return repository.getReferenceById(id);
    }

    public Optional<SolicitacaoTransporte> procurarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public SolicitacaoTransporte cadastrar(DadosCadastroSolicitacao dados) {
        SolicitacaoTransporte solicitacao = new SolicitacaoTransporte(dados);
        
        // Calcula o frete antes de salvar
        freightService.calcularFretePorSolicitacao(solicitacao);
        
        return repository.save(solicitacao);
    }

    @Transactional
    public void deleteBySolicitacaoId(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void atualizarSolicitacao(Long id, DadosAtualizacaoSolicitacao dados) {
        SolicitacaoTransporte solicitacao = repository.findById(id).orElse(null);
        if (solicitacao != null) {
            solicitacao.atualizarInformacoes(dados);
            
            // Recalcula o frete após atualização
            freightService.calcularFretePorSolicitacao(solicitacao);
            
            repository.save(solicitacao);
        }
    }

    /**
     * Busca solicitação por ID (lança exceção se não encontrar)
     */
    public SolicitacaoTransporte buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada com ID: " + id));
    }

    /**
     * Calcula o frete detalhado para uma solicitação
     */
    public FreightService.DetalheFrete calcularFreteDetalhado(Long id) {
        SolicitacaoTransporte solicitacao = buscarPorId(id);
        return freightService.calcularFreteDetalhado(solicitacao);
    }
}

