package com.example.frota.percurso;

import com.example.frota.caminhao.Caminhao;
import com.example.frota.caminhao.CaminhaoRepository;
import com.example.frota.motorista.Motorista;
import com.example.frota.motorista.MotoristaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para gerenciar percursos/viagens
 */
@Service
public class PercursoService {

    @Autowired
    private PercursoRepository repository;

    @Autowired
    private CaminhaoRepository caminhaoRepository;

    @Autowired
    private MotoristaRepository motoristaRepository;

    @Transactional
    public Percurso iniciar(DadosCadastroPercurso dados) {
        Caminhao caminhao = caminhaoRepository.findById(dados.caminhaoId())
                .orElseThrow(() -> new RuntimeException("Caminhão não encontrado"));

        Motorista motorista = motoristaRepository.findById(dados.motoristaId())
                .orElseThrow(() -> new RuntimeException("Motorista não encontrado"));

        if (!motorista.estaDisponivel()) {
            throw new IllegalStateException("Motorista não está disponível");
        }

        // Verificar se já existe percurso em andamento para este caminhão
        List<Percurso> percursosEmAndamento = repository.findByFinalizadoFalse().stream()
                .filter(p -> p.getCaminhao().getId().equals(caminhao.getId()))
                .toList();

        if (!percursosEmAndamento.isEmpty()) {
            throw new IllegalStateException("Caminhão já possui percurso em andamento");
        }

        Percurso percurso = new Percurso(dados, caminhao, motorista);
        return repository.save(percurso);
    }

    public List<DadosListagemPercurso> listarTodos() {
        return repository.findAll().stream()
                .map(DadosListagemPercurso::new)
                .collect(Collectors.toList());
    }

    public List<DadosListagemPercurso> listarEmAndamento() {
        return repository.findByFinalizadoFalse().stream()
                .map(DadosListagemPercurso::new)
                .collect(Collectors.toList());
    }

    public List<DadosListagemPercurso> listarFinalizados() {
        return repository.findByFinalizadoTrue().stream()
                .map(DadosListagemPercurso::new)
                .collect(Collectors.toList());
    }

    public Percurso buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Percurso não encontrado"));
    }

    @Transactional
    public Percurso finalizar(Long id, Integer kmChegada, Double litrosCombustivel, Double custoCombustivel) {
        Percurso percurso = buscarPorId(id);

        if (percurso.getFinalizado()) {
            throw new IllegalStateException("Percurso já foi finalizado");
        }

        percurso.finalizar(kmChegada, litrosCombustivel, custoCombustivel);
        return repository.save(percurso);
    }

    @Transactional
    public Percurso atualizar(Long id, DadosAtualizacaoPercurso dados) {
        Percurso percurso = buscarPorId(id);
        percurso.atualizarInformacoes(dados);
        return repository.save(percurso);
    }

    @Transactional
    public void deletar(Long id) {
        repository.deleteById(id);
    }

    /**
     * Busca percursos de um caminhão
     */
    public List<DadosListagemPercurso> listarPorCaminhao(Long caminhaoId) {
        Caminhao caminhao = caminhaoRepository.findById(caminhaoId)
                .orElseThrow(() -> new RuntimeException("Caminhão não encontrado"));

        return repository.findByCaminhao(caminhao).stream()
                .map(DadosListagemPercurso::new)
                .collect(Collectors.toList());
    }

    /**
     * Busca percursos de um motorista
     */
    public List<DadosListagemPercurso> listarPorMotorista(Long motoristaId) {
        Motorista motorista = motoristaRepository.findById(motoristaId)
                .orElseThrow(() -> new RuntimeException("Motorista não encontrado"));

        return repository.findByMotorista(motorista).stream()
                .map(DadosListagemPercurso::new)
                .collect(Collectors.toList());
    }

    /**
     * Calcula total de km rodados por um caminhão
     */
    public Integer calcularTotalKmRodados(Long caminhaoId) {
        Caminhao caminhao = caminhaoRepository.findById(caminhaoId)
                .orElseThrow(() -> new RuntimeException("Caminhão não encontrado"));

        Integer total = repository.calcularTotalKmPorCaminhao(caminhao);
        return total != null ? total : 0;
    }

    /**
     * Busca último percurso de um caminhão
     */
    public Percurso buscarUltimoPercurso(Long caminhaoId) {
        Caminhao caminhao = caminhaoRepository.findById(caminhaoId)
                .orElseThrow(() -> new RuntimeException("Caminhão não encontrado"));

        return repository.findUltimoPercursoByCaminhao(caminhao);
    }
}

