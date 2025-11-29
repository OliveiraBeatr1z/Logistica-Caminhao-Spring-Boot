package com.example.frota.pagamento;

import com.example.frota.enums.StatusPagamento;
import com.example.frota.solicitacaoTransporte.SolicitacaoTransporte;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade representando o pagamento de uma solicitação de transporte
 */
@Entity
@Table(name = "pagamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitacao_id", nullable = false, unique = true)
    private SolicitacaoTransporte solicitacao;

    @Column(nullable = false)
    private Double valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status;

    @Column(nullable = false)
    private String metodoPagamento; // PIX, Cartão, Boleto, etc.

    private String transacaoId; // ID da transação no gateway de pagamento

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    private LocalDateTime dataConfirmacao;

    private LocalDateTime dataCancelamento;

    @Column(length = 1000)
    private String observacoes;

    public Pagamento(DadosCriacaoPagamento dados, SolicitacaoTransporte solicitacao) {
        this.solicitacao = solicitacao;
        this.valor = dados.valor();
        this.status = StatusPagamento.PENDENTE;
        this.metodoPagamento = dados.metodoPagamento();
        this.dataCriacao = LocalDateTime.now();
        this.observacoes = dados.observacoes();
    }

    /**
     * Confirma o pagamento
     */
    public void confirmar(String transacaoId) {
        if (!status.podeSerCancelado()) {
            throw new IllegalStateException("Pagamento não pode ser confirmado no status atual");
        }
        this.status = StatusPagamento.CONFIRMADO;
        this.transacaoId = transacaoId;
        this.dataConfirmacao = LocalDateTime.now();
    }

    /**
     * Cancela o pagamento
     */
    public void cancelar(String motivo) {
        if (!status.podeSerCancelado()) {
            throw new IllegalStateException("Pagamento não pode ser cancelado no status atual");
        }
        this.status = StatusPagamento.CANCELADO;
        this.dataCancelamento = LocalDateTime.now();
        this.observacoes = (this.observacoes != null ? this.observacoes + "\n" : "") + "Cancelado: " + motivo;
    }

    /**
     * Marca o pagamento como processando
     */
    public void processar() {
        if (this.status != StatusPagamento.PENDENTE) {
            throw new IllegalStateException("Apenas pagamentos pendentes podem ser processados");
        }
        this.status = StatusPagamento.PROCESSANDO;
    }

    /**
     * Reembolsa o pagamento
     */
    public void reembolsar(String motivo) {
        if (this.status != StatusPagamento.CONFIRMADO) {
            throw new IllegalStateException("Apenas pagamentos confirmados podem ser reembolsados");
        }
        this.status = StatusPagamento.REEMBOLSADO;
        this.observacoes = (this.observacoes != null ? this.observacoes + "\n" : "") + "Reembolsado: " + motivo;
    }
}

