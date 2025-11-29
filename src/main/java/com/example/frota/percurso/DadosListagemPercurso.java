package com.example.frota.percurso;

import java.time.LocalDateTime;

public record DadosListagemPercurso(
    Long id,
    Long caminhaoId,
    String caminhaoModelo,
    String caminhaoPlaca,
    Long motoristaId,
    String motoristaNome,
    LocalDateTime dataHoraSaida,
    LocalDateTime dataHoraChegada,
    Integer kmSaida,
    Integer kmChegada,
    Integer distanciaPercorrida,
    Double litrosCombustivel,
    Double custoCombustivel,
    Double consumoMedio,
    Boolean finalizado
) {
    public DadosListagemPercurso(Percurso percurso) {
        this(
            percurso.getId(),
            percurso.getCaminhao().getId(),
            percurso.getCaminhao().getModelo(),
            percurso.getCaminhao().getPlaca(),
            percurso.getMotorista().getId(),
            percurso.getMotorista().getNome(),
            percurso.getDataHoraSaida(),
            percurso.getDataHoraChegada(),
            percurso.getKmSaida(),
            percurso.getKmChegada(),
            percurso.calcularDistanciaPercorrida(),
            percurso.getLitrosCombustivel(),
            percurso.getCustoCombustivel(),
            percurso.calcularConsumoMedio(),
            percurso.getFinalizado()
        );
    }
}

