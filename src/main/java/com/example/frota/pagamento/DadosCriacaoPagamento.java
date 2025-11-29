package com.example.frota.pagamento;

import jakarta.validation.constraints.*;

public record DadosCriacaoPagamento(
    @NotNull(message = "Solicitação é obrigatória")
    Long solicitacaoId,

    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    Double valor,

    @NotBlank(message = "Método de pagamento é obrigatório")
    String metodoPagamento,

    String observacoes
) {}

