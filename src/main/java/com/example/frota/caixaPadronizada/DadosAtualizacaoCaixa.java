package com.example.frota.caixaPadronizada;

public record DadosAtualizacaoCaixa(
    Long id,
    String descricao,
    String material,
    Double comprimento,
    Double largura,
    Double altura,
    Double limitePeso,
    Double valorCaixa
) {}
