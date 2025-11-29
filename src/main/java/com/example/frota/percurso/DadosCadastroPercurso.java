package com.example.frota.percurso;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record DadosCadastroPercurso(
    @NotNull(message = "Caminhão é obrigatório")
    Long caminhaoId,

    @NotNull(message = "Motorista é obrigatório")
    Long motoristaId,

    LocalDateTime dataHoraSaida,

    @NotNull(message = "Km de saída é obrigatório")
    @Positive(message = "Km de saída deve ser positivo")
    Integer kmSaida,

    String observacoes
) {}

