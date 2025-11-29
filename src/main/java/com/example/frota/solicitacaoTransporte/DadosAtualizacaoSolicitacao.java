package com.example.frota.solicitacaoTransporte;

public record DadosAtualizacaoSolicitacao(
        Long id,
        String produto,
        Double comprimento,
        Double largura,
        Double altura,
        Double pesoReal,
        Double origemLat,
        Double origemLon,
        Double destinoLat,
        Double destinoLon,
        String origemEndereco,
        String destinoEndereco,
        Long caixaId,
        Long caminhaoId
) {}

