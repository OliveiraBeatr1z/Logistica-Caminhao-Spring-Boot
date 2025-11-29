package com.example.frota.motorista;

import java.time.LocalDate;

public record DadosListagemMotorista(
    Long id,
    String nome,
    String cpf,
    String cnh,
    String categoriaCnh,
    LocalDate validadeCnh,
    String telefone,
    Boolean ativo,
    Boolean cnhValida
) {
    public DadosListagemMotorista(Motorista motorista) {
        this(
            motorista.getId(),
            motorista.getNome(),
            motorista.getCpf(),
            motorista.getCnh(),
            motorista.getCategoriaCnh(),
            motorista.getValidadeCnh(),
            motorista.getTelefone(),
            motorista.getAtivo(),
            motorista.cnhValida()
        );
    }
}

