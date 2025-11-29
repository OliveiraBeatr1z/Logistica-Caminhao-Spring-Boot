package com.example.frota.rastreamento;

import com.example.frota.enums.StatusEntrega;
import com.example.frota.motorista.Motorista;
import com.example.frota.solicitacaoTransporte.SolicitacaoTransporte;
import com.example.frota.solicitacaoTransporte.SolicitacaoTransporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller para rastreamento de entregas com mapa interativo
 */
@Controller
@RequestMapping("/rastreamento")
public class RastreamentoController {

    @Autowired
    private SolicitacaoTransporteRepository solicitacaoRepository;

    /**
     * Página de rastreamento com mapa
     */
    @GetMapping("/{solicitacaoId}")
    public String mostrarRastreamento(@PathVariable Long solicitacaoId, Model model) {
        try {
            SolicitacaoTransporte solicitacao = solicitacaoRepository.findById(solicitacaoId)
                    .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));

            model.addAttribute("solicitacao", solicitacao);
            return "rastreamento/mapa";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            return "redirect:/solicitacoes/mostrar";
        }
    }

    /**
     * API REST - Retorna posição atual e dados da entrega
     */
    @GetMapping("/api/{solicitacaoId}/posicao")
    @ResponseBody
    public ResponseEntity<?> obterPosicao(@PathVariable Long solicitacaoId) {
        try {
            SolicitacaoTransporte solicitacao = solicitacaoRepository.findById(solicitacaoId)
                    .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));

            RastreamentoDTO dto = new RastreamentoDTO(
                    solicitacao.getId(),
                    solicitacao.getProduto(),
                    solicitacao.getOrigemLat(),
                    solicitacao.getOrigemLon(),
                    solicitacao.getOrigemEndereco(),
                    solicitacao.getDestinoLat(),
                    solicitacao.getDestinoLon(),
                    solicitacao.getDestinoEndereco(),
                    solicitacao.getMotorista() != null ? solicitacao.getMotorista().getUltimaLatitude() : null,
                    solicitacao.getMotorista() != null ? solicitacao.getMotorista().getUltimaLongitude() : null,
                    solicitacao.getMotorista() != null ? solicitacao.getMotorista().getNome() : null,
                    solicitacao.getStatus() != null ? solicitacao.getStatus().name() : null,
                    solicitacao.getStatus() != null ? solicitacao.getStatus().getNome() : null,
                    solicitacao.getDistanciaKm(),
                    calcularDistanciaRestante(solicitacao),
                    calcularTempoEstimado(solicitacao),
                    solicitacao.getMotorista() != null && solicitacao.getMotorista().getUltimaAtualizacaoLocalizacao() != null ?
                            formatarDataHora(solicitacao.getMotorista().getUltimaAtualizacaoLocalizacao()) : null,
                    solicitacao.getDataHoraColeta(),
                    solicitacao.getDataHoraProcessamento(),
                    solicitacao.getDataHoraACaminho(),
                    solicitacao.getDataHoraEntregue(),
                    solicitacao.getHorarioColetaProgramado()
            );

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErroResponse(e.getMessage()));
        }
    }

    /**
     * Calcula distância restante estimada
     */
    private Double calcularDistanciaRestante(SolicitacaoTransporte solicitacao) {
        if (solicitacao.getDistanciaKm() == null) {
            return null;
        }

        // Se já entregue, distância restante é 0
        if (solicitacao.getStatus() == StatusEntrega.ENTREGUE) {
            return 0.0;
        }

        // Se não há posição do motorista, retorna distância total
        Motorista motorista = solicitacao.getMotorista();
        if (motorista == null || motorista.getUltimaLatitude() == null) {
            return solicitacao.getDistanciaKm();
        }

        // Calcula distância da posição atual até o destino (fórmula de Haversine simplificada)
        double lat1 = Math.toRadians(motorista.getUltimaLatitude());
        double lon1 = Math.toRadians(motorista.getUltimaLongitude());
        double lat2 = Math.toRadians(solicitacao.getDestinoLat());
        double lon2 = Math.toRadians(solicitacao.getDestinoLon());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distancia = 6371 * c; // Raio da Terra em km

        return Math.max(0, distancia);
    }

    /**
     * Calcula tempo estimado de entrega
     */
    private String calcularTempoEstimado(SolicitacaoTransporte solicitacao) {
        if (solicitacao.getStatus() == StatusEntrega.ENTREGUE) {
            return "Entregue";
        }

        Double distanciaRestante = calcularDistanciaRestante(solicitacao);
        if (distanciaRestante == null || distanciaRestante == 0) {
            return "N/A";
        }

        // Velocidade média estimada: 60 km/h
        double horasEstimadas = distanciaRestante / 60.0;
        long minutosEstimados = Math.round(horasEstimadas * 60);

        if (minutosEstimados < 60) {
            return minutosEstimados + " minutos";
        } else {
            long horas = minutosEstimados / 60;
            long minutos = minutosEstimados % 60;
            return horas + "h " + minutos + "min";
        }
    }

    /**
     * Formata data e hora
     */
    private String formatarDataHora(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTime.format(formatter);
    }

    /**
     * DTO para resposta de rastreamento
     */
    public record RastreamentoDTO(
            Long id,
            String produto,
            Double origemLat,
            Double origemLon,
            String origemEndereco,
            Double destinoLat,
            Double destinoLon,
            String destinoEndereco,
            Double motoristaLat,
            Double motoristaLon,
            String motoristaNome,
            String status,
            String statusNome,
            Double distanciaTotal,
            Double distanciaRestante,
            String tempoEstimado,
            String ultimaAtualizacao,
            LocalDateTime dataHoraColeta,
            LocalDateTime dataHoraProcessamento,
            LocalDateTime dataHoraACaminho,
            LocalDateTime dataHoraEntregue,
            LocalDateTime horarioColetaProgramado
    ) {}

    /**
     * Record para erro
     */
    public record ErroResponse(String erro) {}
}

