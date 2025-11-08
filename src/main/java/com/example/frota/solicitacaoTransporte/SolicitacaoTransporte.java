package com.example.frota.solicitacaoTransporte;

import com.example.frota.caminhao.Caminhao;
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
@Table(name = "solicitacao_transporte")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class SolicitacaoTransporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String produto;

    // dimensões do produto em metros
    private double comprimento;
    private double largura;
    private double altura;

    // peso real em kg
    private double pesoReal;

    // origem/destino latitude/longitude
    private double origemLat;
    private double origemLon;
    private double destinoLat;
    private double destinoLon;

    // endereços legíveis
    private String origemEndereco;
    private String destinoEndereco;

    // se for cobrado por caixa, id da caixa escolhida (opcional)
    private Long caixaId;

    // caminhão utilizado
    private Long caminhaoId;

    // valor do frete calculado
    private Double valorFrete;

    // distância calculada em km
    private Double distanciaKm;

    // valor do pedágio
    private Double valorPedagio;

    // fator de cubagem (padrão: 300 kg/m³)
    private double fatorCubagem = 300.0;

    public SolicitacaoTransporte(DadosCadastroSolicitacao dados) {
        this.produto = dados.produto();
        this.comprimento = dados.comprimento();
        this.largura = dados.largura();
        this.altura = dados.altura();
        this.pesoReal = dados.pesoReal();
        this.origemLat = dados.origemLat();
        this.origemLon = dados.origemLon();
        this.destinoLat = dados.destinoLat();
        this.destinoLon = dados.destinoLon();
        this.origemEndereco = dados.origemEndereco();
        this.destinoEndereco = dados.destinoEndereco();
        this.caixaId = dados.caixaId();
        this.caminhaoId = dados.caminhaoId();
    }

    public void atualizarInformacoes(DadosAtualizacaoSolicitacao dados) {
        if (dados.produto() != null)
            this.produto = dados.produto();
        if (dados.comprimento() != null)
            this.comprimento = dados.comprimento();
        if (dados.largura() != null)
            this.largura = dados.largura();
        if (dados.altura() != null)
            this.altura = dados.altura();
        if (dados.pesoReal() != null)
            this.pesoReal = dados.pesoReal();
        if (dados.origemLat() != null)
            this.origemLat = dados.origemLat();
        if (dados.origemLon() != null)
            this.origemLon = dados.origemLon();
        if (dados.destinoLat() != null)
            this.destinoLat = dados.destinoLat();
        if (dados.destinoLon() != null)
            this.destinoLon = dados.destinoLon();
        if (dados.origemEndereco() != null)
            this.origemEndereco = dados.origemEndereco();
        if (dados.destinoEndereco() != null)
            this.destinoEndereco = dados.destinoEndereco();
        if (dados.caixaId() != null)
            this.caixaId = dados.caixaId();
        if (dados.caminhaoId() != null)
            this.caminhaoId = dados.caminhaoId();
    }

    /**
     * Calcula o volume do produto em m³
     */
    public double calcularVolume() {
        return this.comprimento * this.largura * this.altura;
    }

    /**
     * Calcula o peso cubado (Volume x Fator de Cubagem) em kg
     */
    public double calcularPesoCubado() {
        return calcularVolume() * this.fatorCubagem;
    }

    /**
     * Calcula o peso considerado para o frete (maior entre peso real e peso cubado)
     */
    public double calcularPesoConsiderado() {
        return Math.max(this.pesoReal, calcularPesoCubado());
    }

    /**
     * Valida se o produto cabe no caminhão fornecido
     */
    public boolean validarDimensoes(Caminhao caminhao) {
        return caminhao.podeTransportarPeso(calcularPesoConsiderado()) &&
               caminhao.podeTransportarVolume(this.comprimento, this.largura, this.altura);
    }
}
