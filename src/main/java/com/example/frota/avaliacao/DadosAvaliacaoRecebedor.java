package com.example.frota.avaliacao;

import jakarta.validation.constraints.*;

public record DadosAvaliacaoRecebedor(
    @NotNull(message = "Solicitação é obrigatória")
    Long solicitacaoId,

    @NotNull(message = "Nota é obrigatória")
    @Min(value = 1, message = "Nota mínima é 1")
    @Max(value = 5, message = "Nota máxima é 5")
    Integer nota,

    String comentario
) {}

