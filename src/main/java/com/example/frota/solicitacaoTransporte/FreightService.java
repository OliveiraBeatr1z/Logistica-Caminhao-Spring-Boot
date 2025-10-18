package com.example.frota.solicitacaoTransporte;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.frota.caixaPadronizada.CaixaPadronizada;
import com.example.frota.caixaPadronizada.CaixaPadronizadaRepository;

@Service
public class FreightService {

    private final CaixaPadronizadaRepository caixaRepo;
    private final DistanceClient distanceClient;
    private final TollClient tollClient;

    // exemplo: R$ por km se cobrar por caixa
    @Value("${freight.rate.perKmPerBox:2.0}")
    private double taxaPorKmPorCaixa;

    // exemplo: R$ por kg por km
    @Value("${freight.rate.perKgPerKm:0.01}")
    private double precoPorKgPorKm;

    public FreightService(CaixaPadronizadaRepository caixaRepo, DistanceClient distanceClient, TollClient tollClient) {
        this.caixaRepo = caixaRepo;
        this.distanceClient = distanceClient;
        this.tollClient = tollClient;
    }

    public List<CaixaPadronizada> listarCaixas() {
        return caixaRepo.findAll();
    }

    public Optional<CaixaPadronizada> encontraCaixaQueCabe(double c, double l, double a) {
        return caixaRepo.findAll().stream()
                .filter(cx -> cx.getComprimento() >= c && cx.getLargura() >= l && cx.getAltura() >= a)
                .findFirst();
    }

    public double calcularFretePorSolicitacao(SolicitacaoTransporte s) {
        // calcula distância (km) via client
        double distancia = distanceClient.distanceKm(s.getOrigemLat(), s.getOrigemLon(), s.getDestinoLat(), s.getDestinoLon());

        // volume do produto (m3)
        double volume = s.getComprimento() * s.getLargura() * s.getAltura();

        // fator de cubagem rodoviário padrão 300 kg/m3
        double fator = 300.0;
        double pesoCubado = volume * fator;

        double pesoConsiderado = Math.max(s.getPesoReal(), pesoCubado);

        // frete por peso
        double custoPeso = pesoConsiderado * precoPorKgPorKm * distancia;

        // frete por caixa (se houver caixa que caiba)
        Optional<CaixaPadronizada> caixaOpt = encontraCaixaQueCabe(s.getComprimento(), s.getLargura(), s.getAltura());
        double custoPorCaixa = Double.POSITIVE_INFINITY;
        if (caixaOpt.isPresent()) {
            double qtdCaixas = 1; // simplificação
            custoPorCaixa = qtdCaixas * taxaPorKmPorCaixa * distancia;
        }

        // pedágio via TollClient
        double pedagio = tollClient.estimateToll(s.getOrigemLat(), s.getOrigemLon(), s.getDestinoLat(), s.getDestinoLon());

        double base = Double.isInfinite(custoPorCaixa) ? custoPeso : Math.max(custoPeso, custoPorCaixa);
        double custoFinal = base + pedagio;
        return custoFinal;
    }
}
