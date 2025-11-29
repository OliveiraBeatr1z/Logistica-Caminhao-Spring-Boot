package com.example.frota.enums;

/**
 * Tipo de manutenção realizada no caminhão
 */
public enum TipoManutencao {
    PREVENTIVA_10K("Manutenção Preventiva 10.000 Km", 10000, "Troca de óleos, filtros e pastilhas"),
    TROCA_PNEUS("Troca de Pneus 70.000 Km", 70000, "Substituição dos pneus"),
    CORRETIVA("Manutenção Corretiva", 0, "Reparo de problemas específicos"),
    REVISAO("Revisão Geral", 0, "Revisão completa do veículo"),
    OUTROS("Outros", 0, "Outros tipos de manutenção");

    private final String descricao;
    private final int kmIntervalo;
    private final String detalhes;

    TipoManutencao(String descricao, int kmIntervalo, String detalhes) {
        this.descricao = descricao;
        this.kmIntervalo = kmIntervalo;
        this.detalhes = detalhes;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getKmIntervalo() {
        return kmIntervalo;
    }

    public String getDetalhes() {
        return detalhes;
    }

    /**
     * Verifica se é uma manutenção preventiva programada
     */
    public boolean isPreventiva() {
        return this == PREVENTIVA_10K || this == TROCA_PNEUS;
    }
}

