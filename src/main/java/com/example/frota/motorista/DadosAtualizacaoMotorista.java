package com.example.frota.motorista;

import java.time.LocalDate;

public record DadosAtualizacaoMotorista(
    String nome,
    String telefone,
    String email,
    String categoriaCnh,
    LocalDate validadeCnh,
    Boolean ativo
) {}

