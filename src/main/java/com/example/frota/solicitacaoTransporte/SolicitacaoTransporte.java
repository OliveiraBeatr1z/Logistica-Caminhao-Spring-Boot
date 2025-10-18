package com.example.frota.solicitacaoTransporte;

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

    // se for cobrado por caixa, id da caixa escolhida (opcional)
    private Long caixaId;

}
