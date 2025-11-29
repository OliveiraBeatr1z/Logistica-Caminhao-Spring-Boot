package com.example.frota.percurso;

import java.time.LocalDateTime;

public record DadosAtualizacaoPercurso(
    LocalDateTime dataHoraSaida,
    LocalDateTime dataHoraChegada,
    Integer kmSaida,
    Integer kmChegada,
    Double litrosCombustivel,
    Double custoCombustivel,
    String observacoes
) {}

