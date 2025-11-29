package com.example.frota.otimizacao;

import com.example.frota.caminhao.Caminhao;
import com.example.frota.caminhao.CaminhaoRepository;
import com.example.frota.solicitacaoTransporte.SolicitacaoTransporte;
import com.example.frota.solicitacaoTransporte.SolicitacaoTransporteRepository;
import com.example.frota.enums.StatusEntrega;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service para otimização de carga
 * Implementa algoritmos para lotar caminhões de forma eficiente
 */
@Service
public class OtimizacaoCargaService {

    @Autowired
    private SolicitacaoTransporteRepository solicitacaoRepository;

    @Autowired
    private CaminhaoRepository caminhaoRepository;

    /**
     * Busca o melhor caminhão para um conjunto de solicitações
     * Tenta maximizar o uso do espaço interno
     */
    public SugestaoCaminhao sugerirCaminhaoOtimizado(List<Long> solicitacaoIds) {
        List<SolicitacaoTransporte> solicitacoes = solicitacaoRepository.findAllById(solicitacaoIds);

        if (solicitacoes.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma solicitação encontrada");
        }

        // Calcula totais necessários
        double pesoTotal = solicitacoes.stream()
                .mapToDouble(SolicitacaoTransporte::calcularPesoConsiderado)
                .sum();

        double volumeTotal = solicitacoes.stream()
                .mapToDouble(SolicitacaoTransporte::calcularVolume)
                .sum();

        // Busca todos os caminhões disponíveis
        List<Caminhao> caminhoes = caminhaoRepository.findAll();

        // Filtra caminhões que podem carregar o peso
        List<Caminhao> caminhoesAptos = caminhoes.stream()
                .filter(c -> c.getCargaMaxima() >= pesoTotal)
                .filter(c -> c.calcularVolume() >= volumeTotal)
                .collect(Collectors.toList());

        if (caminhoesAptos.isEmpty()) {
            throw new IllegalStateException("Nenhum caminhão disponível para esta carga");
        }

        // Encontra o caminhão mais otimizado (menor desperdício de espaço)
        Caminhao melhorCaminhao = caminhoesAptos.stream()
                .min(Comparator.comparingDouble(c -> 
                    calcularDesperdicio(c, pesoTotal, volumeTotal)))
                .orElseThrow();

        double capacidadeUtilizada = (pesoTotal / melhorCaminhao.getCargaMaxima()) * 100;
        double volumeUtilizado = (volumeTotal / melhorCaminhao.calcularVolume()) * 100;

        return new SugestaoCaminhao(
            melhorCaminhao,
            solicitacoes,
            pesoTotal,
            volumeTotal,
            capacidadeUtilizada,
            volumeUtilizado,
            calcularEconomia(melhorCaminhao, solicitacoes)
        );
    }

    /**
     * Agrupa solicitações pendentes por região de destino
     */
    public Map<String, List<SolicitacaoTransporte>> agruparPorRegiao() {
        List<SolicitacaoTransporte> pendentes = solicitacaoRepository.findAll().stream()
                .filter(s -> s.getStatus() == StatusEntrega.COLETA)
                .collect(Collectors.toList());

        // Agrupa por endereço de destino simplificado
        return pendentes.stream()
                .collect(Collectors.groupingBy(s -> extrairRegiao(s.getDestinoEndereco())));
    }

    /**
     * Sugere agrupamento ótimo de solicitações para maximizar uso de caminhões
     */
    public List<GrupoEntrega> sugerirAgrupamentoOtimo() {
        List<SolicitacaoTransporte> pendentes = solicitacaoRepository.findAll().stream()
                .filter(s -> s.getStatus() == StatusEntrega.COLETA)
                .collect(Collectors.toList());

        List<Caminhao> caminhoes = caminhaoRepository.findAll();
        List<GrupoEntrega> grupos = new ArrayList<>();

        // Agrupa por região
        Map<String, List<SolicitacaoTransporte>> porRegiao = agruparPorRegiao();

        for (Map.Entry<String, List<SolicitacaoTransporte>> entry : porRegiao.entrySet()) {
            String regiao = entry.getKey();
            List<SolicitacaoTransporte> solicitacoesRegiao = entry.getValue();

            // Tenta agrupar solicitações da mesma região em caminhões
            List<List<SolicitacaoTransporte>> combinacoes = gerarCombinacoesOtimas(
                solicitacoesRegiao, 
                caminhoes
            );

            for (List<SolicitacaoTransporte> combinacao : combinacoes) {
                if (!combinacao.isEmpty()) {
                    try {
                        SugestaoCaminhao sugestao = sugerirCaminhaoOtimizado(
                            combinacao.stream().map(SolicitacaoTransporte::getId).toList()
                        );

                        grupos.add(new GrupoEntrega(
                            regiao,
                            combinacao,
                            sugestao.caminhao(),
                            sugestao.capacidadeUtilizada(),
                            sugestao.economiaEstimada()
                        ));
                    } catch (Exception e) {
                        // Não foi possível agrupar esta combinação
                    }
                }
            }
        }

        // Ordena por taxa de utilização (mais otimizados primeiro)
        grupos.sort(Comparator.comparingDouble(GrupoEntrega::taxaUtilizacao).reversed());

        return grupos;
    }

    /**
     * Gera combinações ótimas de solicitações que cabem em caminhões
     */
    private List<List<SolicitacaoTransporte>> gerarCombinacoesOtimas(
            List<SolicitacaoTransporte> solicitacoes,
            List<Caminhao> caminhoes) {

        List<List<SolicitacaoTransporte>> combinacoes = new ArrayList<>();

        // Algoritmo guloso: pega solicitações até encher um caminhão
        List<SolicitacaoTransporte> restantes = new ArrayList<>(solicitacoes);

        while (!restantes.isEmpty()) {
            List<SolicitacaoTransporte> grupo = new ArrayList<>();
            double pesoAcumulado = 0;
            double volumeAcumulado = 0;

            // Pega o menor caminhão que pode ser usado
            Optional<Caminhao> caminhaoOpt = caminhoes.stream()
                    .min(Comparator.comparingDouble(Caminhao::getCargaMaxima));

            if (caminhaoOpt.isEmpty()) break;

            Caminhao caminhao = caminhaoOpt.get();

            Iterator<SolicitacaoTransporte> it = restantes.iterator();
            while (it.hasNext()) {
                SolicitacaoTransporte sol = it.next();
                double peso = sol.calcularPesoConsiderado();
                double volume = sol.calcularVolume();

                if (pesoAcumulado + peso <= caminhao.getCargaMaxima() &&
                    volumeAcumulado + volume <= caminhao.calcularVolume()) {
                    grupo.add(sol);
                    pesoAcumulado += peso;
                    volumeAcumulado += volume;
                    it.remove();
                }
            }

            if (!grupo.isEmpty()) {
                combinacoes.add(grupo);
            } else {
                // Se nenhuma solicitação couber, remove a primeira e tenta separadamente
                if (!restantes.isEmpty()) {
                    combinacoes.add(List.of(restantes.remove(0)));
                }
            }
        }

        return combinacoes;
    }

    /**
     * Calcula o desperdício de espaço (menor é melhor)
     */
    private double calcularDesperdicio(Caminhao caminhao, double pesoTotal, double volumeTotal) {
        double desperdicoPeso = caminhao.getCargaMaxima() - pesoTotal;
        double desperdicioVolume = caminhao.calcularVolume() - volumeTotal;
        return desperdicoPeso + (desperdicioVolume * 100); // Volume pesa mais na decisão
    }

    /**
     * Extrai região do endereço (simplificado)
     */
    private String extrairRegiao(String endereco) {
        if (endereco == null) return "Desconhecido";

        // Simplificação: pega últimos 2 termos separados por vírgula (geralmente cidade e estado)
        String[] partes = endereco.split(",");
        if (partes.length >= 2) {
            return partes[partes.length - 2].trim() + ", " + partes[partes.length - 1].trim();
        }
        return endereco;
    }

    /**
     * Calcula economia estimada ao agrupar entregas
     */
    private double calcularEconomia(Caminhao caminhao, List<SolicitacaoTransporte> solicitacoes) {
        // Economia baseada em compartilhamento de rota
        if (solicitacoes.size() <= 1) return 0.0;

        double distanciaTotal = solicitacoes.stream()
                .mapToDouble(s -> s.getDistanciaKm() != null ? s.getDistanciaKm() : 0)
                .sum();

        // Estima que ao agrupar, economiza-se ~20% da distância total
        double economiaDistancia = distanciaTotal * 0.20;
        return economiaDistancia * caminhao.getValorPorKm();
    }

    /**
     * Record para sugestão de caminhão
     */
    public record SugestaoCaminhao(
        Caminhao caminhao,
        List<SolicitacaoTransporte> solicitacoes,
        double pesoTotal,
        double volumeTotal,
        double capacidadeUtilizada, // percentual
        double volumeUtilizado, // percentual
        double economiaEstimada
    ) {}

    /**
     * Record para grupo de entrega
     */
    public record GrupoEntrega(
        String regiao,
        List<SolicitacaoTransporte> solicitacoes,
        Caminhao caminhao,
        double taxaUtilizacao,
        double economiaEstimada
    ) {}
}

