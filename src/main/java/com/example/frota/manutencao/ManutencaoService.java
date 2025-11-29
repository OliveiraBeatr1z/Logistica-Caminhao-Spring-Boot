package com.example.frota.manutencao;

import com.example.frota.caminhao.Caminhao;
import com.example.frota.caminhao.CaminhaoRepository;
import com.example.frota.enums.TipoManutencao;
import com.example.frota.percurso.PercursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para gerenciar manutenções de caminhões
 * Controla manutenção preventiva a cada 10.000 km e troca de pneus a cada 70.000 km
 */
@Service
public class ManutencaoService {

    @Autowired
    private ManutencaoRepository repository;

    @Autowired
    private CaminhaoRepository caminhaoRepository;

    @Autowired
    private PercursoService percursoService;

    @Transactional
    public Manutencao cadastrar(DadosCadastroManutencao dados) {
        Caminhao caminhao = caminhaoRepository.findById(dados.caminhaoId())
                .orElseThrow(() -> new RuntimeException("Caminhão não encontrado"));

        Manutencao manutencao = new Manutencao(dados, caminhao);
        return repository.save(manutencao);
    }

    public List<DadosListagemManutencao> listarTodas() {
        return repository.findAll().stream()
                .map(DadosListagemManutencao::new)
                .collect(Collectors.toList());
    }

    public List<DadosListagemManutencao> listarPorCaminhao(Long caminhaoId) {
        Caminhao caminhao = caminhaoRepository.findById(caminhaoId)
                .orElseThrow(() -> new RuntimeException("Caminhão não encontrado"));

        return repository.findByCaminhao(caminhao).stream()
                .map(DadosListagemManutencao::new)
                .collect(Collectors.toList());
    }

    public Manutencao buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manutenção não encontrada"));
    }

    @Transactional
    public Manutencao atualizar(Long id, DadosAtualizacaoManutencao dados) {
        Manutencao manutencao = buscarPorId(id);
        manutencao.atualizarInformacoes(dados);
        return repository.save(manutencao);
    }

    @Transactional
    public void deletar(Long id) {
        repository.deleteById(id);
    }

    /**
     * Verifica se um caminhão precisa de manutenção
     * Retorna lista de alertas
     */
    public List<AlertaManutencao> verificarAlertas(Long caminhaoId) {
        List<AlertaManutencao> alertas = new ArrayList<>();

        Caminhao caminhao = caminhaoRepository.findById(caminhaoId)
                .orElseThrow(() -> new RuntimeException("Caminhão não encontrado"));

        // Calcula km total rodado
        Integer kmTotal = percursoService.calcularTotalKmRodados(caminhaoId);
        if (kmTotal == null) kmTotal = 0;

        // Verifica manutenção preventiva (a cada 10.000 km)
        Manutencao ultimaPreventiva = repository.findUltimaManutencaoPreventivaByCaminhao(caminhao);
        if (ultimaPreventiva == null) {
            // Nunca fez manutenção preventiva
            if (kmTotal >= 10000) {
                alertas.add(new AlertaManutencao(
                    caminhaoId,
                    TipoManutencao.PREVENTIVA_10K,
                    "URGENTE: Manutenção preventiva nunca foi realizada! " + kmTotal + " km rodados.",
                    kmTotal,
                    0,
                    NivelAlerta.CRITICO
                ));
            }
        } else {
            Integer kmDesdeUltimaManutencao = kmTotal - ultimaPreventiva.getKmManutencao();
            if (kmDesdeUltimaManutencao >= 10000) {
                alertas.add(new AlertaManutencao(
                    caminhaoId,
                    TipoManutencao.PREVENTIVA_10K,
                    "Manutenção preventiva atrasada! " + kmDesdeUltimaManutencao + " km desde a última.",
                    kmTotal,
                    kmDesdeUltimaManutencao,
                    NivelAlerta.CRITICO
                ));
            } else if (kmDesdeUltimaManutencao >= 9000) {
                alertas.add(new AlertaManutencao(
                    caminhaoId,
                    TipoManutencao.PREVENTIVA_10K,
                    "Manutenção preventiva próxima! " + (10000 - kmDesdeUltimaManutencao) + " km restantes.",
                    kmTotal,
                    kmDesdeUltimaManutencao,
                    NivelAlerta.AVISO
                ));
            }
        }

        // Verifica troca de pneus (a cada 70.000 km)
        Manutencao ultimaTrocaPneus = repository.findUltimaTrocaPneusByCaminhao(caminhao);
        if (ultimaTrocaPneus == null) {
            if (kmTotal >= 70000) {
                alertas.add(new AlertaManutencao(
                    caminhaoId,
                    TipoManutencao.TROCA_PNEUS,
                    "URGENTE: Pneus nunca foram trocados! " + kmTotal + " km rodados.",
                    kmTotal,
                    0,
                    NivelAlerta.CRITICO
                ));
            } else if (kmTotal >= 65000) {
                alertas.add(new AlertaManutencao(
                    caminhaoId,
                    TipoManutencao.TROCA_PNEUS,
                    "Troca de pneus próxima! " + (70000 - kmTotal) + " km restantes.",
                    kmTotal,
                    kmTotal,
                    NivelAlerta.AVISO
                ));
            }
        } else {
            Integer kmDesdeUltimaTroca = kmTotal - ultimaTrocaPneus.getKmManutencao();
            if (kmDesdeUltimaTroca >= 70000) {
                alertas.add(new AlertaManutencao(
                    caminhaoId,
                    TipoManutencao.TROCA_PNEUS,
                    "Troca de pneus atrasada! " + kmDesdeUltimaTroca + " km desde a última troca.",
                    kmTotal,
                    kmDesdeUltimaTroca,
                    NivelAlerta.CRITICO
                ));
            } else if (kmDesdeUltimaTroca >= 65000) {
                alertas.add(new AlertaManutencao(
                    caminhaoId,
                    TipoManutencao.TROCA_PNEUS,
                    "Troca de pneus próxima! " + (70000 - kmDesdeUltimaTroca) + " km restantes.",
                    kmTotal,
                    kmDesdeUltimaTroca,
                    NivelAlerta.AVISO
                ));
            }
        }

        return alertas;
    }

    /**
     * Verifica alertas para todos os caminhões
     */
    public List<AlertaManutencao> verificarTodosAlertas() {
        List<AlertaManutencao> todosAlertas = new ArrayList<>();

        List<Caminhao> caminhoes = caminhaoRepository.findAll();
        for (Caminhao caminhao : caminhoes) {
            todosAlertas.addAll(verificarAlertas(caminhao.getId()));
        }

        return todosAlertas;
    }

    /**
     * Record para representar um alerta de manutenção
     */
    public record AlertaManutencao(
        Long caminhaoId,
        TipoManutencao tipo,
        String mensagem,
        Integer kmAtual,
        Integer kmDesdeUltima,
        NivelAlerta nivel
    ) {}

    /**
     * Enum para nível de criticidade do alerta
     */
    public enum NivelAlerta {
        INFORMATIVO,
        AVISO,
        CRITICO
    }
}

