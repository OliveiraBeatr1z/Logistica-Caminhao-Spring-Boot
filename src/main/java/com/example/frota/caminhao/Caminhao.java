package com.example.frota.caminhao;

import com.example.frota.marca.Marca;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "caminhao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of ="id")
public class Caminhao {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "caminhao_id")
	private Long id;
	private String modelo;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "marca_id", referencedColumnName = "marca_id")
	private Marca marca;
	private String placa;
	private double cargaMaxima;
	private int ano;

	// Dimensões em metros
	private double comprimento; // metros
	private double largura; // metros
	private double altura; // metros

	// Fator de cubagem em kg/m³ (padrão rodoviário: 300 kg/m³)
	private double fatorCubagem = 300.0;

	// Valor por quilômetro rodado
	private double valorPorKm;

	/**
	 * Calcula o volume em metros cúbicos (m³)
	 */
	public double calcularVolume() {
		return this.comprimento * this.largura * this.altura;
	}

	/**
	 * Calcula o peso cubado em kg
	 */
	public double calcularPesoCubado() {
		return calcularVolume() * this.fatorCubagem;
	}

	public Caminhao(CadastroCaminhao dados, Marca marca) {
		this.modelo = dados.modelo();
		this.placa = dados.placa();
		this.cargaMaxima = dados.cargaMaxima();
		this.marca = marca;
		this.ano = dados.ano();
		this.comprimento = dados.comprimento() == null ? 0.0 : dados.comprimento();
		this.largura = dados.largura() == null ? 0.0 : dados.largura();
		this.altura = dados.altura() == null ? 0.0 : dados.altura();
		this.fatorCubagem = dados.fatorCubagem() == null ? this.fatorCubagem : dados.fatorCubagem();
		this.valorPorKm = dados.valorPorKm() == null ? 0.0 : dados.valorPorKm();
	}

	/**
	 * Calcula o frete básico baseado na distância
	 */
	public double calcularFreteBasico(double distanciaKm) {
		return distanciaKm * this.valorPorKm;
	}

	/**
	 * Verifica se o caminhão pode transportar a carga com base no peso
	 */
	public boolean podeTransportarPeso(double pesoCarga) {
		return pesoCarga <= this.cargaMaxima;
	}

	/**
	 * Verifica se o caminhão pode transportar a carga com base nas dimensões
	 */
	public boolean podeTransportarVolume(double comprimentoCarga, double larguraCarga, double alturaCarga) {
		return comprimentoCarga <= this.comprimento &&
			   larguraCarga <= this.largura &&
			   alturaCarga <= this.altura;
	}
	
	public void atualizarInformacoes(AtualizacaoCaminhao dados, Marca marca) {
		if (dados.modelo() != null)
			this.modelo = dados.modelo();
		if (dados.placa() != null)
			this.placa = dados.placa();
		if (dados.cargaMaxima() != 0)
			this.cargaMaxima = dados.cargaMaxima();
		if (dados.comprimento() != null)
			this.comprimento = dados.comprimento();
		if (dados.largura() != null)
			this.largura = dados.largura();
		if (dados.altura() != null)
			this.altura = dados.altura();
		if (dados.fatorCubagem() != null)
			this.fatorCubagem = dados.fatorCubagem();
		if (dados.valorPorKm() != null)
			this.valorPorKm = dados.valorPorKm();
		if (marca != null)
			this.marca = marca;
		if (dados.ano() != 0)
			this.ano = dados.ano();
	}
	
}
