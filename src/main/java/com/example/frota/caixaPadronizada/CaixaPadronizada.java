package com.example.frota.caixaPadronizada;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "caixa_padronizada")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class CaixaPadronizada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // dimensões em metros
    private double comprimento;
    private double largura;
    private double altura;

    private String material;

    // limite de peso em kg
    private double limitePeso;

    public CaixaPadronizada(DadosCadastroCaixa dados) {
        this.comprimento = dados.comprimento();
        this.largura = dados.largura();
        this.altura = dados.altura();
        this.material = dados.material();
        this.limitePeso = dados.limitePeso();
    }

    public void atualizarInformacoes(DadosAtualizacaoCaixa dados) {
        if (dados.material() != null)
            this.material = dados.material();
        if (dados.comprimento() != null)
            this.comprimento = dados.comprimento();
        if (dados.largura() != null)
            this.largura = dados.largura();
        if (dados.altura() != null)
            this.altura = dados.altura();
        if (dados.limitePeso() != null)
            this.limitePeso = dados.limitePeso();
    }

    public double getVolume() {
        return comprimento * largura * altura;
    }
}
