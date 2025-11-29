package com.example.frota.manutencao;

import com.example.frota.enums.TipoManutencao;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record DadosCadastroManutencao(
    @NotNull(message = "Caminhão é obrigatório")
    Long caminhaoId,

    @NotNull(message = "Tipo de manutenção é obrigatório")
    TipoManutencao tipo,

    LocalDate dataManutencao,

    @NotNull(message = "Quilometragem é obrigatória")
    @Positive(message = "Quilometragem deve ser positiva")
    Integer kmManutencao,

    @NotBlank(message = "Descrição é obrigatória")
    String descricao,

    @PositiveOrZero(message = "Custo deve ser positivo ou zero")
    Double custo,

    String oficina
) {}

