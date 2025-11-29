package com.example.frota.manutencao;

import com.example.frota.caminhao.Caminhao;
import com.example.frota.enums.TipoManutencao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ManutencaoRepository extends JpaRepository<Manutencao, Long> {

    /**
     * Busca manutenções por caminhão
     */
    List<Manutencao> findByCaminhao(Caminhao caminhao);

    /**
     * Busca manutenções por tipo
     */
    List<Manutencao> findByTipo(TipoManutencao tipo);

    /**
     * Busca última manutenção de um caminhão
     */
    @Query("SELECT m FROM Manutencao m WHERE m.caminhao = :caminhao ORDER BY m.dataManutencao DESC, m.kmManutencao DESC LIMIT 1")
    Manutencao findUltimaManutencaoByCaminhao(Caminhao caminhao);

    /**
     * Busca última manutenção preventiva de um caminhão
     */
    @Query("SELECT m FROM Manutencao m WHERE m.caminhao = :caminhao AND m.tipo = 'PREVENTIVA_10K' ORDER BY m.kmManutencao DESC LIMIT 1")
    Manutencao findUltimaManutencaoPreventivaByCaminhao(Caminhao caminhao);

    /**
     * Busca última troca de pneus de um caminhão
     */
    @Query("SELECT m FROM Manutencao m WHERE m.caminhao = :caminhao AND m.tipo = 'TROCA_PNEUS' ORDER BY m.kmManutencao DESC LIMIT 1")
    Manutencao findUltimaTrocaPneusByCaminhao(Caminhao caminhao);

    /**
     * Busca manutenções em um período
     */
    @Query("SELECT m FROM Manutencao m WHERE m.dataManutencao BETWEEN :dataInicio AND :dataFim")
    List<Manutencao> findByPeriodo(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Calcula custo total de manutenções por caminhão
     */
    @Query("SELECT SUM(m.custo) FROM Manutencao m WHERE m.caminhao = :caminhao")
    Double calcularCustoTotalManutencao(Caminhao caminhao);
}

