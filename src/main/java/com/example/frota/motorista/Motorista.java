package com.example.frota.motorista;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entidade representando um motorista da frota
 */
@Entity
@Table(name = "motorista")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Motorista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String cnh;

    @Column(nullable = false)
    private String categoriaCnh;

    @Column(nullable = false)
    private LocalDate validadeCnh;

    private String telefone;
    private String email;

    @Column(nullable = false)
    private Boolean ativo = true;

    // Dados para rastreamento (última posição conhecida)
    private Double ultimaLatitude;
    private Double ultimaLongitude;

    @Column(name = "ultima_atualizacao_localizacao")
    private java.time.LocalDateTime ultimaAtualizacaoLocalizacao;

    public Motorista(DadosCadastroMotorista dados) {
        this.nome = dados.nome();
        this.cpf = dados.cpf();
        this.cnh = dados.cnh();
        this.categoriaCnh = dados.categoriaCnh();
        this.validadeCnh = dados.validadeCnh();
        this.telefone = dados.telefone();
        this.email = dados.email();
        this.ativo = true;
    }

    public void atualizarInformacoes(DadosAtualizacaoMotorista dados) {
        if (dados.nome() != null) this.nome = dados.nome();
        if (dados.telefone() != null) this.telefone = dados.telefone();
        if (dados.email() != null) this.email = dados.email();
        if (dados.categoriaCnh() != null) this.categoriaCnh = dados.categoriaCnh();
        if (dados.validadeCnh() != null) this.validadeCnh = dados.validadeCnh();
        if (dados.ativo() != null) this.ativo = dados.ativo();
    }

    /**
     * Atualiza a localização atual do motorista (enviada pelo app)
     */
    public void atualizarLocalizacao(double latitude, double longitude) {
        this.ultimaLatitude = latitude;
        this.ultimaLongitude = longitude;
        this.ultimaAtualizacaoLocalizacao = java.time.LocalDateTime.now();
    }

    /**
     * Verifica se a CNH está válida
     */
    public boolean cnhValida() {
        return this.validadeCnh != null && this.validadeCnh.isAfter(LocalDate.now());
    }

    /**
     * Verifica se o motorista está disponível para trabalhar
     */
    public boolean estaDisponivel() {
        return this.ativo && cnhValida();
    }
}

