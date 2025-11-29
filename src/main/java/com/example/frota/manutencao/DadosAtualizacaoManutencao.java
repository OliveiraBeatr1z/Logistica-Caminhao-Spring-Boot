package com.example.frota.manutencao;

import com.example.frota.enums.TipoManutencao;
import java.time.LocalDate;

public record DadosAtualizacaoManutencao(
    TipoManutencao tipo,
    LocalDate dataManutencao,
    Integer kmManutencao,
    String descricao,
    Double custo,
    String oficina
) {}

