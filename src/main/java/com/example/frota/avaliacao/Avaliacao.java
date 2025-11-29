package com.example.frota.avaliacao;

import com.example.frota.solicitacaoTransporte.SolicitacaoTransporte;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade representando a avaliação/feedback de uma entrega
 * Permite que cliente e recebedor avaliem o serviço
 */
@Entity
@Table(name = "avaliacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitacao_id", nullable = false, unique = true)
    private SolicitacaoTransporte solicitacao;

    // Avaliação do cliente (quem solicitou)
    private Integer notaCliente; // 1 a 5

    @Column(length = 1000)
    private String comentarioCliente;

    private LocalDateTime dataAvaliacaoCliente;

    // Avaliação do recebedor
    private Integer notaRecebedor; // 1 a 5

    @Column(length = 1000)
    private String comentarioRecebedor;

    private LocalDateTime dataAvaliacaoRecebedor;

    public Avaliacao(SolicitacaoTransporte solicitacao) {
        this.solicitacao = solicitacao;
    }

    /**
     * Registra a avaliação do cliente
     */
    public void avaliarPorCliente(Integer nota, String comentario) {
        validarNota(nota);
        this.notaCliente = nota;
        this.comentarioCliente = comentario;
        this.dataAvaliacaoCliente = LocalDateTime.now();
    }

    /**
     * Registra a avaliação do recebedor
     */
    public void avaliarPorRecebedor(Integer nota, String comentario) {
        validarNota(nota);
        this.notaRecebedor = nota;
        this.comentarioRecebedor = comentario;
        this.dataAvaliacaoRecebedor = LocalDateTime.now();
    }

    /**
     * Valida se a nota está entre 1 e 5
     */
    private void validarNota(Integer nota) {
        if (nota == null || nota < 1 || nota > 5) {
            throw new IllegalArgumentException("Nota deve estar entre 1 e 5");
        }
    }

    /**
     * Calcula a média das avaliações
     */
    public Double calcularMediaAvaliacoes() {
        int count = 0;
        int sum = 0;

        if (notaCliente != null) {
            sum += notaCliente;
            count++;
        }

        if (notaRecebedor != null) {
            sum += notaRecebedor;
            count++;
        }

        if (count == 0) {
            return null;
        }

        return (double) sum / count;
    }

    /**
     * Verifica se a avaliação está completa (ambos avaliaram)
     */
    public boolean estaCompleta() {
        return notaCliente != null && notaRecebedor != null;
    }
}

