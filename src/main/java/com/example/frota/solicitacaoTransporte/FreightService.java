package com.example.frota.solicitacaoTransporte;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.frota.caixaPadronizada.CaixaPadronizada;
import com.example.frota.caixaPadronizada.CaixaPadronizadaRepository;
import com.example.frota.caminhao.Caminhao;
import com.example.frota.caminhao.CaminhaoRepository;

@Service
public class FreightService {

    private final CaixaPadronizadaRepository caixaRepo;
    private final CaminhaoRepository caminhaoRepo;
    private final DistanceClient distanceClient;
    private final TollClient tollClient;

    // Taxa por km se cobrar por caixa (R$ por km por caixa)
    @Value("${freight.rate.perKmPerBox:2.0}")
    private double taxaPorKmPorCaixa;

    // Taxa por kg por km (R$ por kg por km)
    @Value("${freight.rate.perKgPerKm:0.01}")
    private double precoPorKgPorKm;

    // Taxa por km rodado (R$ por km)
    @Value("${freight.rate.perKm:1.5}")
    private double taxaPorKm;

    public FreightService(
            CaixaPadronizadaRepository caixaRepo, 
            CaminhaoRepository caminhaoRepo,
            DistanceClient distanceClient, 
            TollClient tollClient) {
        this.caixaRepo = caixaRepo;
        this.caminhaoRepo = caminhaoRepo;
        this.distanceClient = distanceClient;
        this.tollClient = tollClient;
    }

    public List<CaixaPadronizada> listarCaixas() {
        return caixaRepo.findAll();
    }

    public List<Caminhao> listarCaminhoes() {
        return caminhaoRepo.findAll();
    }

    /**
     * Encontra uma caixa que caiba o produto com as dimensões especificadas
     */
    public Optional<CaixaPadronizada> encontraCaixaQueCabe(double c, double l, double a, double peso) {
        return caixaRepo.findAll().stream()
                .filter(cx -> cx.getComprimento() >= c 
                           && cx.getLargura() >= l 
                           && cx.getAltura() >= a
                           && cx.getLimitePeso() >= peso)
                .findFirst();
    }

    /**
     * Encontra um caminhão que caiba o produto com as dimensões especificadas
     */
    public Optional<Caminhao> encontraCaminhaoQueCabe(double c, double l, double a, double peso) {
        return caminhaoRepo.findAll().stream()
                .filter(cam -> cam.getComprimento() >= c 
                            && cam.getLargura() >= l 
                            && cam.getAltura() >= a
                            && cam.getCargaMaxima() >= peso)
                .findFirst();
    }

    /**
     * Calcula o frete completo para uma solicitação de transporte
     */
    public double calcularFretePorSolicitacao(SolicitacaoTransporte s) {
        // 1. Calcula distância (km) via client
        double distancia = distanceClient.distanceKm(
            s.getOrigemLat(), 
            s.getOrigemLon(), 
            s.getDestinoLat(), 
            s.getDestinoLon()
        );
        s.setDistanciaKm(distancia);

        // 2. Calcula o peso cubado do produto
        double pesoCubado = s.getPesoCubado();

        // 3. Determina o peso considerado (maior entre peso real e peso cubado)
        double pesoConsiderado = s.getPesoConsiderado();

        // 4. Calcula frete por peso
        double custoPorPeso = pesoConsiderado * precoPorKgPorKm * distancia;

        // 5. Calcula frete por caixa (se houver caixa que caiba)
        double custoPorCaixa = Double.POSITIVE_INFINITY;
        if (s.getCaixaId() != null) {
            Optional<CaixaPadronizada> caixaOpt = caixaRepo.findById(s.getCaixaId());
            if (caixaOpt.isPresent()) {
                CaixaPadronizada caixa = caixaOpt.get();
                // Verifica se o produto cabe na caixa
                if (caixa.getComprimento() >= s.getComprimento() 
                    && caixa.getLargura() >= s.getLargura() 
                    && caixa.getAltura() >= s.getAltura()
                    && caixa.getLimitePeso() >= pesoConsiderado) {
                    custoPorCaixa = taxaPorKmPorCaixa * distancia;
                }
            }
        } else {
            // Tenta encontrar uma caixa que caiba
            Optional<CaixaPadronizada> caixaOpt = encontraCaixaQueCabe(
                s.getComprimento(), 
                s.getLargura(), 
                s.getAltura(), 
                pesoConsiderado
            );
            if (caixaOpt.isPresent()) {
                s.setCaixaId(caixaOpt.get().getId());
                custoPorCaixa = taxaPorKmPorCaixa * distancia;
            }
        }

        // 6. Define o custo base (menor entre custo por peso e custo por caixa)
        double custoBase;
        if (Double.isInfinite(custoPorCaixa)) {
            custoBase = custoPorPeso;
        } else {
            custoBase = Math.min(custoPorPeso, custoPorCaixa);
        }

        // 7. Adiciona a taxa por km rodado
        double custoKmRodado = taxaPorKm * distancia;

        // 8. Calcula pedágio via TollClient
        double pedagio = tollClient.estimateToll(
            s.getOrigemLat(), 
            s.getOrigemLon(), 
            s.getDestinoLat(), 
            s.getDestinoLon()
        );
        s.setValorPedagio(pedagio);

        // 9. Calcula o custo final
        double custoFinal = custoBase + custoKmRodado + pedagio;
        s.setValorFrete(custoFinal);

        return custoFinal;
    }

    /**
     * Calcula o frete detalhado para uma solicitação
     */
    public DetalheFrete calcularFreteDetalhado(SolicitacaoTransporte s) {
        double distancia = distanceClient.distanceKm(
            s.getOrigemLat(), 
            s.getOrigemLon(), 
            s.getDestinoLat(), 
            s.getDestinoLon()
        );

        double pesoCubado = s.getPesoCubado();
        double pesoConsiderado = s.getPesoConsiderado();
        double custoPorPeso = pesoConsiderado * precoPorKgPorKm * distancia;
        
        Double custoPorCaixa = null;
        if (s.getCaixaId() != null) {
            Optional<CaixaPadronizada> caixaOpt = caixaRepo.findById(s.getCaixaId());
            if (caixaOpt.isPresent()) {
                custoPorCaixa = taxaPorKmPorCaixa * distancia;
            }
        }

        double custoBase = custoPorCaixa != null ? Math.min(custoPorPeso, custoPorCaixa) : custoPorPeso;
        double custoKmRodado = taxaPorKm * distancia;
        double pedagio = tollClient.estimateToll(
            s.getOrigemLat(), 
            s.getOrigemLon(), 
            s.getDestinoLat(), 
            s.getDestinoLon()
        );
        
        double custoTotal = custoBase + custoKmRodado + pedagio;

        return new DetalheFrete(
            distancia,
            s.getPesoReal(),
            pesoCubado,
            pesoConsiderado,
            custoPorPeso,
            custoPorCaixa,
            custoBase,
            custoKmRodado,
            pedagio,
            custoTotal
        );
    }

    /**
     * Record para detalhamento do cálculo do frete
     */
    public record DetalheFrete(
        double distanciaKm,
        double pesoReal,
        double pesoCubado,
        double pesoConsiderado,
        double custoPorPeso,
        Double custoPorCaixa,
        double custoBase,
        double custoKmRodado,
        double valorPedagio,
        double custoTotal
    ) {}
}

