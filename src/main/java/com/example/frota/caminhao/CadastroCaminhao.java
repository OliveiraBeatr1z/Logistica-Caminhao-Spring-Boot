package com.example.frota.caminhao;

import com.example.frota.marca.Marca;

import jakarta.validation.constraints.NotBlank;

public record CadastroCaminhao(
	@NotBlank
	String modelo,
	String placa,
	Marca marca,
	double cargaMaxima,
	int ano,
	// dimensões em metros
	Double comprimento,
	Double largura,
	Double altura,
	// opcional, kg/m3
	Double fatorCubagem) {

}

