package com.example.frota.manutencao;

import com.example.frota.caminhao.Caminhao;
import com.example.frota.enums.TipoManutencao;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entidade representando uma manutenção realizada em um caminhão
 * Sistema controla manutenções preventivas a cada 10.000 km e troca de pneus a cada 70.000 km
 */
@Entity
@Table(name = "manutencao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Manutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caminhao_id", nullable = false)
    private Caminhao caminhao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoManutencao tipo;

    @Column(nullable = false)
    private LocalDate dataManutencao;

    // Quilometragem do caminhão no momento da manutenção
    @Column(nullable = false)
    private Integer kmManutencao;

    // Descrição detalhada do que foi feito
    @Column(length = 2000)
    private String descricao;

    // Custo da manutenção
    private Double custo;

    // Oficina ou responsável
    private String oficina;

    // Próxima manutenção programada (para preventivas)
    private Integer proximaManutencaoKm;

    public Manutencao(DadosCadastroManutencao dados, Caminhao caminhao) {
        this.caminhao = caminhao;
        this.tipo = dados.tipo();
        this.dataManutencao = dados.dataManutencao() != null ? dados.dataManutencao() : LocalDate.now();
        this.kmManutencao = dados.kmManutencao();
        this.descricao = dados.descricao();
        this.custo = dados.custo();
        this.oficina = dados.oficina();
        this.calcularProximaManutencao();
    }

    public void atualizarInformacoes(DadosAtualizacaoManutencao dados) {
        if (dados.tipo() != null) this.tipo = dados.tipo();
        if (dados.dataManutencao() != null) this.dataManutencao = dados.dataManutencao();
        if (dados.kmManutencao() != null) this.kmManutencao = dados.kmManutencao();
        if (dados.descricao() != null) this.descricao = dados.descricao();
        if (dados.custo() != null) this.custo = dados.custo();
        if (dados.oficina() != null) this.oficina = dados.oficina();
        this.calcularProximaManutencao();
    }

    /**
     * Calcula quando será a próxima manutenção preventiva
     */
    private void calcularProximaManutencao() {
        if (tipo.isPreventiva()) {
            this.proximaManutencaoKm = this.kmManutencao + tipo.getKmIntervalo();
        }
    }

    /**
     * Verifica se a manutenção está atrasada com base na quilometragem atual
     */
    public boolean estaAtrasada(Integer kmAtual) {
        if (proximaManutencaoKm == null) {
            return false;
        }
        return kmAtual >= proximaManutencaoKm;
    }

    /**
     * Retorna quantos km faltam para a próxima manutenção
     */
    public Integer kmParaProximaManutencao(Integer kmAtual) {
        if (proximaManutencaoKm == null) {
            return null;
        }
        return Math.max(0, proximaManutencaoKm - kmAtual);
    }
}

