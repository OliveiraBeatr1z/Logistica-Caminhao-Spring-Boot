package com.example.frota.avaliacao;

import java.time.LocalDateTime;

public record DadosListagemAvaliacao(
    Long id,
    Long solicitacaoId,
    String produtoNome,
    Integer notaCliente,
    String comentarioCliente,
    LocalDateTime dataAvaliacaoCliente,
    Integer notaRecebedor,
    String comentarioRecebedor,
    LocalDateTime dataAvaliacaoRecebedor,
    Double mediaAvaliacoes,
    Boolean completa
) {
    public DadosListagemAvaliacao(Avaliacao avaliacao) {
        this(
            avaliacao.getId(),
            avaliacao.getSolicitacao().getId(),
            avaliacao.getSolicitacao().getProduto(),
            avaliacao.getNotaCliente(),
            avaliacao.getComentarioCliente(),
            avaliacao.getDataAvaliacaoCliente(),
            avaliacao.getNotaRecebedor(),
            avaliacao.getComentarioRecebedor(),
            avaliacao.getDataAvaliacaoRecebedor(),
            avaliacao.calcularMediaAvaliacoes(),
            avaliacao.estaCompleta()
        );
    }
}

