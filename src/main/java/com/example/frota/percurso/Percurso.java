package com.example.frota.percurso;

import com.example.frota.caminhao.Caminhao;
import com.example.frota.motorista.Motorista;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade representando um percurso/viagem realizado por um caminhão
 * Controla quilometragem, combustível e custos operacionais
 */
@Entity
@Table(name = "percurso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Percurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caminhao_id", nullable = false)
    private Caminhao caminhao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motorista_id", nullable = false)
    private Motorista motorista;

    @Column(nullable = false)
    private LocalDateTime dataHoraSaida;

    private LocalDateTime dataHoraChegada;

    @Column(nullable = false)
    private Integer kmSaida;

    private Integer kmChegada;

    // Combustível em litros
    private Double litrosCombustivel;

    // Custo do combustível
    private Double custoCombustivel;

    // Observações sobre o percurso
    @Column(length = 1000)
    private String observacoes;

    @Column(nullable = false)
    private Boolean finalizado = false;

    public Percurso(DadosCadastroPercurso dados, Caminhao caminhao, Motorista motorista) {
        this.caminhao = caminhao;
        this.motorista = motorista;
        this.dataHoraSaida = dados.dataHoraSaida() != null ? dados.dataHoraSaida() : LocalDateTime.now();
        this.kmSaida = dados.kmSaida();
        this.observacoes = dados.observacoes();
        this.finalizado = false;
    }

    public void atualizarInformacoes(DadosAtualizacaoPercurso dados) {
        if (dados.dataHoraSaida() != null) this.dataHoraSaida = dados.dataHoraSaida();
        if (dados.dataHoraChegada() != null) this.dataHoraChegada = dados.dataHoraChegada();
        if (dados.kmSaida() != null) this.kmSaida = dados.kmSaida();
        if (dados.kmChegada() != null) this.kmChegada = dados.kmChegada();
        if (dados.litrosCombustivel() != null) this.litrosCombustivel = dados.litrosCombustivel();
        if (dados.custoCombustivel() != null) this.custoCombustivel = dados.custoCombustivel();
        if (dados.observacoes() != null) this.observacoes = dados.observacoes();
    }

    /**
     * Finaliza o percurso com os dados de chegada
     */
    public void finalizar(Integer kmChegada, Double litrosCombustivel, Double custoCombustivel) {
        if (kmChegada < this.kmSaida) {
            throw new IllegalArgumentException("Km de chegada não pode ser menor que km de saída");
        }
        this.kmChegada = kmChegada;
        this.dataHoraChegada = LocalDateTime.now();
        this.litrosCombustivel = litrosCombustivel;
        this.custoCombustivel = custoCombustivel;
        this.finalizado = true;
    }

    /**
     * Calcula a distância percorrida em km
     */
    public Integer calcularDistanciaPercorrida() {
        if (kmChegada == null || kmSaida == null) {
            return 0;
        }
        return kmChegada - kmSaida;
    }

    /**
     * Calcula o consumo médio (km/l)
     */
    public Double calcularConsumoMedio() {
        if (litrosCombustivel == null || litrosCombustivel == 0) {
            return null;
        }
        return (double) calcularDistanciaPercorrida() / litrosCombustivel;
    }

    /**
     * Calcula o custo por km do percurso
     */
    public Double calcularCustoPorKm() {
        Integer distancia = calcularDistanciaPercorrida();
        if (custoCombustivel == null || distancia == 0) {
            return null;
        }
        return custoCombustivel / distancia;
    }

    /**
     * Verifica se o percurso está em andamento
     */
    public boolean emAndamento() {
        return !finalizado && dataHoraChegada == null;
    }
}

