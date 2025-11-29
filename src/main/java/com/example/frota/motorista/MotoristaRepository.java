package com.example.frota.motorista;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MotoristaRepository extends JpaRepository<Motorista, Long> {

    /**
     * Busca motoristas ativos
     */
    List<Motorista> findByAtivoTrue();

    /**
     * Busca motorista por CPF
     */
    Motorista findByCpf(String cpf);

    /**
     * Busca motorista por CNH
     */
    Motorista findByCnh(String cnh);

    /**
     * Busca motoristas com CNH válida e ativos
     */
    @Query("SELECT m FROM Motorista m WHERE m.ativo = true AND m.validadeCnh > :dataAtual")
    List<Motorista> findMotoristasDisponiveis(LocalDate dataAtual);

    /**
     * Busca motoristas com CNH vencendo em breve
     */
    @Query("SELECT m FROM Motorista m WHERE m.validadeCnh BETWEEN :dataInicio AND :dataFim")
    List<Motorista> findMotoristasComCnhVencendo(LocalDate dataInicio, LocalDate dataFim);
}

