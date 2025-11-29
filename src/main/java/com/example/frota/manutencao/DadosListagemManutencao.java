package com.example.frota.manutencao;

import com.example.frota.enums.TipoManutencao;
import java.time.LocalDate;

public record DadosListagemManutencao(
    Long id,
    Long caminhaoId,
    String caminhaoModelo,
    String caminhaoPlaca,
    TipoManutencao tipo,
    String tipoDescricao,
    LocalDate dataManutencao,
    Integer kmManutencao,
    String descricao,
    Double custo,
    String oficina,
    Integer proximaManutencaoKm
) {
    public DadosListagemManutencao(Manutencao manutencao) {
        this(
            manutencao.getId(),
            manutencao.getCaminhao().getId(),
            manutencao.getCaminhao().getModelo(),
            manutencao.getCaminhao().getPlaca(),
            manutencao.getTipo(),
            manutencao.getTipo().getDescricao(),
            manutencao.getDataManutencao(),
            manutencao.getKmManutencao(),
            manutencao.getDescricao(),
            manutencao.getCusto(),
            manutencao.getOficina(),
            manutencao.getProximaManutencaoKm()
        );
    }
}

