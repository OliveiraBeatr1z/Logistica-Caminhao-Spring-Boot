package com.example.frota.enums;

/**
 * Status do pagamento da solicitação
 */
public enum StatusPagamento {
    PENDENTE("Pendente", "Aguardando pagamento"),
    PROCESSANDO("Processando", "Pagamento em processamento"),
    CONFIRMADO("Confirmado", "Pagamento confirmado"),
    CANCELADO("Cancelado", "Pagamento cancelado"),
    REEMBOLSADO("Reembolsado", "Pagamento reembolsado");

    private final String nome;
    private final String descricao;

    StatusPagamento(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isPago() {
        return this == CONFIRMADO;
    }

    public boolean podeSerCancelado() {
        return this == PENDENTE || this == PROCESSANDO;
    }
}

