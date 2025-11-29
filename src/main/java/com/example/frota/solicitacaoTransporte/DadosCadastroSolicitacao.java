package com.example.frota.solicitacaoTransporte;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record DadosCadastroSolicitacao(
    @NotBlank(message = "Produto é obrigatório")
    String produto,

    @Positive(message = "Comprimento deve ser positivo")
    double comprimento,

    @Positive(message = "Largura deve ser positiva")
    double largura,

    @Positive(message = "Altura deve ser positiva")
    double altura,

    @Positive(message = "Peso real deve ser positivo")
    double pesoReal,

    double origemLat,
    double origemLon,
    double destinoLat,
    double destinoLon,

    String origemEndereco,
    String destinoEndereco,

    Long caixaId,
    Long caminhaoId,

    // Novos campos Parte 2
    LocalDateTime horarioColetaProgramado,
    String nomeCliente,
    String telefoneCliente,
    
    @Email(message = "Email inválido")
    String emailCliente,
    
    String nomeRecebedor,
    String telefoneRecebedor,
    String observacoes
) {}
