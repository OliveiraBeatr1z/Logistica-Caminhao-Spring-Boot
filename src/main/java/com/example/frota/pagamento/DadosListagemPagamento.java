package com.example.frota.pagamento;

import com.example.frota.enums.StatusPagamento;
import java.time.LocalDateTime;

public record DadosListagemPagamento(
    Long id,
    Long solicitacaoId,
    String produtoNome,
    Double valor,
    StatusPagamento status,
    String statusNome,
    String metodoPagamento,
    String transacaoId,
    LocalDateTime dataCriacao,
    LocalDateTime dataConfirmacao,
    Boolean pago
) {
    public DadosListagemPagamento(Pagamento pagamento) {
        this(
            pagamento.getId(),
            pagamento.getSolicitacao().getId(),
            pagamento.getSolicitacao().getProduto(),
            pagamento.getValor(),
            pagamento.getStatus(),
            pagamento.getStatus().getNome(),
            pagamento.getMetodoPagamento(),
            pagamento.getTransacaoId(),
            pagamento.getDataCriacao(),
            pagamento.getDataConfirmacao(),
            pagamento.getStatus().isPago()
        );
    }
}

