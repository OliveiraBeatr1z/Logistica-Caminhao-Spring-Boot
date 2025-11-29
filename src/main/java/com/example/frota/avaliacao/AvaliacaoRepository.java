package com.example.frota.avaliacao;

import com.example.frota.solicitacaoTransporte.SolicitacaoTransporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    /**
     * Busca avaliação por solicitação
     */
    Optional<Avaliacao> findBySolicitacao(SolicitacaoTransporte solicitacao);

    /**
     * Busca avaliação por ID da solicitação
     */
    @Query("SELECT a FROM Avaliacao a WHERE a.solicitacao.id = :solicitacaoId")
    Optional<Avaliacao> findBySolicitacaoId(Long solicitacaoId);

    /**
     * Busca avaliações completas (ambos avaliaram)
     */
    @Query("SELECT a FROM Avaliacao a WHERE a.notaCliente IS NOT NULL AND a.notaRecebedor IS NOT NULL")
    List<Avaliacao> findAvaliacoesCompletas();

    /**
     * Busca avaliações pendentes do cliente
     */
    @Query("SELECT a FROM Avaliacao a WHERE a.notaCliente IS NULL")
    List<Avaliacao> findAvaliacoesPendentesCliente();

    /**
     * Busca avaliações pendentes do recebedor
     */
    @Query("SELECT a FROM Avaliacao a WHERE a.notaRecebedor IS NULL")
    List<Avaliacao> findAvaliacoesPendentesRecebedor();

    /**
     * Calcula média geral de avaliações
     */
    @Query("SELECT AVG((COALESCE(a.notaCliente, 0) + COALESCE(a.notaRecebedor, 0)) / " +
           "CASE WHEN a.notaCliente IS NOT NULL AND a.notaRecebedor IS NOT NULL THEN 2.0 " +
           "WHEN a.notaCliente IS NOT NULL OR a.notaRecebedor IS NOT NULL THEN 1.0 " +
           "ELSE 1.0 END) FROM Avaliacao a WHERE a.notaCliente IS NOT NULL OR a.notaRecebedor IS NOT NULL")
    Double calcularMediaGeralAvaliacoes();
}

