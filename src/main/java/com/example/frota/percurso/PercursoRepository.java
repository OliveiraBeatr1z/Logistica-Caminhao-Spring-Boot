package com.example.frota.percurso;

import com.example.frota.caminhao.Caminhao;
import com.example.frota.motorista.Motorista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PercursoRepository extends JpaRepository<Percurso, Long> {

    /**
     * Busca percursos por caminhão
     */
    List<Percurso> findByCaminhao(Caminhao caminhao);

    /**
     * Busca percursos por motorista
     */
    List<Percurso> findByMotorista(Motorista motorista);

    /**
     * Busca percursos finalizados
     */
    List<Percurso> findByFinalizadoTrue();

    /**
     * Busca percursos em andamento
     */
    List<Percurso> findByFinalizadoFalse();

    /**
     * Busca percursos por caminhão em um período
     */
    @Query("SELECT p FROM Percurso p WHERE p.caminhao = :caminhao AND p.dataHoraSaida BETWEEN :dataInicio AND :dataFim")
    List<Percurso> findByCaminhaoAndPeriodo(Caminhao caminhao, LocalDateTime dataInicio, LocalDateTime dataFim);

    /**
     * Busca último percurso de um caminhão
     */
    @Query("SELECT p FROM Percurso p WHERE p.caminhao = :caminhao ORDER BY p.dataHoraSaida DESC LIMIT 1")
    Percurso findUltimoPercursoByCaminhao(Caminhao caminhao);

    /**
     * Calcula total de km rodados por caminhão
     */
    @Query("SELECT SUM(p.kmChegada - p.kmSaida) FROM Percurso p WHERE p.caminhao = :caminhao AND p.finalizado = true")
    Integer calcularTotalKmPorCaminhao(Caminhao caminhao);
}

