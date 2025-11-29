package com.example.frota.solicitacaoTransporte;

import com.example.frota.enums.StatusEntrega;
import java.time.LocalDateTime;

public record DadosListagemSolicitacao(
    Long id,
    String produto,
    Double pesoReal,
    Double pesoCubado,
    String origemEndereco,
    String destinoEndereco,
    Double valorFrete,
    Double distanciaKm,
    Long caixaId,
    Long caminhaoId,
    // Novos campos Parte 2
    StatusEntrega status,
    String statusNome,
    Long motoristaId,
    String motoristaNome,
    LocalDateTime horarioColetaProgramado,
    LocalDateTime dataHoraEntregue,
    String nomeCliente,
    String nomeRecebedor,
    Boolean finalizada
) {
    public DadosListagemSolicitacao(SolicitacaoTransporte solicitacao) {
        this(
            solicitacao.getId(),
            solicitacao.getProduto(),
            solicitacao.getPesoReal(),
            solicitacao.calcularPesoCubado(),
            solicitacao.getOrigemEndereco(),
            solicitacao.getDestinoEndereco(),
            solicitacao.getValorFrete(),
            solicitacao.getDistanciaKm(),
            solicitacao.getCaixaId(),
            solicitacao.getCaminhaoId(),
            solicitacao.getStatus(),
            solicitacao.getStatus() != null ? solicitacao.getStatus().getNome() : null,
            solicitacao.getMotorista() != null ? solicitacao.getMotorista().getId() : null,
            solicitacao.getMotorista() != null ? solicitacao.getMotorista().getNome() : null,
            solicitacao.getHorarioColetaProgramado(),
            solicitacao.getDataHoraEntregue(),
            solicitacao.getNomeCliente(),
            solicitacao.getNomeRecebedor(),
            solicitacao.estaFinalizada()
        );
    }
}
