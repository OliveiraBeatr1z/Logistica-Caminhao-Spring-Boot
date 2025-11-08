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

    private String descricao;

    // dimensões em metros
    private double comprimento;
    private double largura;
    private double altura;

    private String material;

    // limite de peso em kg
    private double limitePeso;

    // Valor fixo para cobrança por caixa
    private double valorCaixa;

    /**
     * Calcula o volume da caixa em metros cúbicos (m³)
     */
    public double calcularVolume() {
        return comprimento * largura * altura;
    }

    /**
     * Verifica se um produto com as dimensões e peso dados cabe nesta caixa
     */
    public boolean produtoCabe(double produtoComprimento, double produtoLargura, 
                             double produtoAltura, double produtoPeso) {
        return produtoComprimento <= this.comprimento &&
               produtoLargura <= this.largura &&
               produtoAltura <= this.altura &&
               produtoPeso <= this.limitePeso;
    }

    public CaixaPadronizada(DadosCadastroCaixa dados) {
        this.descricao = dados.descricao();
        this.comprimento = dados.comprimento();
        this.largura = dados.largura();
        this.altura = dados.altura();
        this.material = dados.material();
        this.limitePeso = dados.limitePeso();
        this.valorCaixa = dados.valorCaixa();
    }

    public void atualizarInformacoes(DadosAtualizacaoCaixa dados) {
        if (dados.descricao() != null)
            this.descricao = dados.descricao();
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
        if (dados.valorCaixa() != null)
            this.valorCaixa = dados.valorCaixa();
        if (dados.limitePeso() != null)
            this.limitePeso = dados.limitePeso();
    }

    public double getVolume() {
        return comprimento * largura * altura;
    }
}
