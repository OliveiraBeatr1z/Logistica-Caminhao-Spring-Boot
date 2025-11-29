package com.example.frota.caixaPadronizada;

public record DadosListagemCaixa(
        Long id,
        String material,
        double comprimento,
        double largura,
        double altura,
        double limitePeso,
        double volume
) {
    public DadosListagemCaixa(CaixaPadronizada caixa) {
        this(
            caixa.getId(),
            caixa.getMaterial(),
            caixa.getComprimento(),
            caixa.getLargura(),
            caixa.getAltura(),
            caixa.getLimitePeso(),
            caixa.getVolume()
        );
    }
}

