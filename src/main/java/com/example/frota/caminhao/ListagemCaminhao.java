package com.example.frota.caminhao;

public record ListagemCaminhao(
    Long id,
    String modelo,
    String placa,
    String marca,
    double cargaMaxima,
    int ano,
    Double comprimento,
    Double largura,
    Double altura,
    Double fatorCubagem,
    Double volume,
    Double pesoCubado
) {
    public ListagemCaminhao(Caminhao caminhao) {
        this(
            caminhao.getId(),
            caminhao.getModelo(),
            caminhao.getPlaca(),
            caminhao.getMarca() != null ? caminhao.getMarca().getNome() : "",
            caminhao.getCargaMaxima(),
            caminhao.getAno(),
            caminhao.getComprimento(),
            caminhao.getLargura(),
            caminhao.getAltura(),
            caminhao.getFatorCubagem(),
            caminhao.getVolume(),
            caminhao.getPesoCubado()
        );
    }
}

