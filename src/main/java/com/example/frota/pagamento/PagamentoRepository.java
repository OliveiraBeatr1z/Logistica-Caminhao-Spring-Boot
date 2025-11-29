package com.example.frota.pagamento;

import com.example.frota.enums.StatusPagamento;
import com.example.frota.solicitacaoTransporte.SolicitacaoTransporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    /**
     * Busca pagamento por solicitação
     */
    Optional<Pagamento> findBySolicitacao(SolicitacaoTransporte solicitacao);

    /**
     * Busca pagamento por ID da solicitação
     */
    @Query("SELECT p FROM Pagamento p WHERE p.solicitacao.id = :solicitacaoId")
    Optional<Pagamento> findBySolicitacaoId(Long solicitacaoId);

    /**
     * Busca pagamentos por status
     */
    List<Pagamento> findByStatus(StatusPagamento status);

    /**
     * Busca pagamentos pendentes
     */
    @Query("SELECT p FROM Pagamento p WHERE p.status = 'PENDENTE' OR p.status = 'PROCESSANDO'")
    List<Pagamento> findPagamentosPendentes();

    /**
     * Busca pagamentos confirmados
     */
    List<Pagamento> findByStatusAndDataConfirmacaoBetween(
        StatusPagamento status, 
        LocalDateTime dataInicio, 
        LocalDateTime dataFim
    );

    /**
     * Busca pagamento por ID de transação
     */
    Optional<Pagamento> findByTransacaoId(String transacaoId);

    /**
     * Calcula receita total no período
     */
    @Query("SELECT SUM(p.valor) FROM Pagamento p WHERE p.status = 'CONFIRMADO' AND p.dataConfirmacao BETWEEN :dataInicio AND :dataFim")
    Double calcularReceitaPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim);

    /**
     * Busca pagamentos por método de pagamento
     */
    List<Pagamento> findByMetodoPagamento(String metodoPagamento);
}

