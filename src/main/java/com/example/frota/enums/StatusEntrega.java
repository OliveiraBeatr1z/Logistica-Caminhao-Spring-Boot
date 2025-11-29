package com.example.frota.enums;

/**
 * Enum representando as 4 etapas do processo de entrega
 */
public enum StatusEntrega {
    COLETA("Coleta", "Aguardando coleta do produto"),
    EM_PROCESSAMENTO("Em Processamento", "Produto coletado e sendo processado"),
    A_CAMINHO("A Caminho da Entrega", "Produto em trânsito para o destino"),
    ENTREGUE("Entregue", "Produto entregue ao destinatário");

    private final String nome;
    private final String descricao;

    StatusEntrega(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Retorna o próximo status na sequência
     */
    public StatusEntrega proximo() {
        return switch (this) {
            case COLETA -> EM_PROCESSAMENTO;
            case EM_PROCESSAMENTO -> A_CAMINHO;
            case A_CAMINHO -> ENTREGUE;
            case ENTREGUE -> ENTREGUE; // já está no status final
        };
    }

    /**
     * Verifica se já foi entregue
     */
    public boolean isFinalizado() {
        return this == ENTREGUE;
    }
}

