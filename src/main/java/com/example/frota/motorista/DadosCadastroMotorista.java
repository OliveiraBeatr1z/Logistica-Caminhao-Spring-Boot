package com.example.frota.motorista;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record DadosCadastroMotorista(
    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    String cpf,

    @NotBlank(message = "CNH é obrigatória")
    String cnh,

    @NotBlank(message = "Categoria da CNH é obrigatória")
    String categoriaCnh,

    @NotNull(message = "Validade da CNH é obrigatória")
    @Future(message = "A CNH deve estar válida")
    LocalDate validadeCnh,

    String telefone,

    @Email(message = "Email inválido")
    String email
) {}

