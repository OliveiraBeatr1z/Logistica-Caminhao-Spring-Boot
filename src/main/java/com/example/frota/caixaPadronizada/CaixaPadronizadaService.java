package com.example.frota.caixaPadronizada;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class CaixaPadronizadaService {
    
    @Autowired
    private CaixaPadronizadaRepository repository;

    public List<CaixaPadronizada> getAllCaixas() {
        return repository.findAll(Sort.by("material").ascending());
    }

    public CaixaPadronizada getCaixaById(Long id) {
        return repository.getReferenceById(id);
    }

    public Optional<CaixaPadronizada> procurarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public CaixaPadronizada cadastrar(DadosCadastroCaixa dados) {
        CaixaPadronizada caixa = new CaixaPadronizada(dados);
        return repository.save(caixa);
    }

    @Transactional
    public void deleteByCaixaId(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void atualizarCaixa(Long id, DadosAtualizacaoCaixa dados) {
        CaixaPadronizada caixa = repository.findById(id).orElse(null);
        if (caixa != null) {
            caixa.atualizarInformacoes(dados);
            repository.save(caixa);
        }
    }

    /**
     * Encontra uma caixa que caiba o produto com as dimensões especificadas
     */
    public Optional<CaixaPadronizada> encontrarCaixaQueCabe(double comprimento, double largura, double altura, double peso) {
        return repository.findAll().stream()
                .filter(cx -> cx.getComprimento() >= comprimento 
                           && cx.getLargura() >= largura 
                           && cx.getAltura() >= altura
                           && cx.getLimitePeso() >= peso)
                .findFirst();
    }
}

