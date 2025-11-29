package com.example.frota.caixaPadronizada;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record DadosCadastroCaixa(
    String descricao,
    
    @NotBlank(message = "Material é obrigatório")
    String material,
    
    @Positive(message = "Comprimento deve ser positivo")
    double comprimento,
    
    @Positive(message = "Largura deve ser positiva")
    double largura,
    
    @Positive(message = "Altura deve ser positiva")
    double altura,
    
    @Positive(message = "Limite de peso deve ser positivo")
    double limitePeso,
    
    @PositiveOrZero(message = "Valor da caixa não pode ser negativo")
    double valorCaixa
) {}
