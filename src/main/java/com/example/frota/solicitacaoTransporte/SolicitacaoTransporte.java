package com.example.frota.solicitacaoTransporte;

import com.example.frota.caminhao.Caminhao;
import com.example.frota.enums.StatusEntrega;
import com.example.frota.motorista.Motorista;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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

    // ========== NOVOS CAMPOS PARTE 2 ==========

    // Status da entrega (4 etapas)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEntrega status = StatusEntrega.COLETA;

    // Motorista responsável pela entrega
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motorista_id")
    private Motorista motorista;

    // Percurso/viagem associado
    @Column(name = "percurso_id")
    private Long percursoId;

    // Horário programado para coleta
    private LocalDateTime horarioColetaProgramado;

    // Horários de cada etapa
    private LocalDateTime dataHoraColeta;
    private LocalDateTime dataHoraProcessamento;
    private LocalDateTime dataHoraACaminho;
    private LocalDateTime dataHoraEntregue;

    // Nome do cliente solicitante
    private String nomeCliente;
    private String telefoneCliente;
    private String emailCliente;

    // Nome do recebedor
    private String nomeRecebedor;
    private String telefoneRecebedor;

    // Observações
    @Column(length = 2000)
    private String observacoes;

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
        this.horarioColetaProgramado = dados.horarioColetaProgramado();
        this.nomeCliente = dados.nomeCliente();
        this.telefoneCliente = dados.telefoneCliente();
        this.emailCliente = dados.emailCliente();
        this.nomeRecebedor = dados.nomeRecebedor();
        this.telefoneRecebedor = dados.telefoneRecebedor();
        this.observacoes = dados.observacoes();
        this.status = StatusEntrega.COLETA;
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

    // ========== MÉTODOS PARA CONTROLE DE STATUS ==========

    /**
     * Avança para o próximo status e registra a data/hora
     */
    public void avancarStatus() {
        StatusEntrega proximoStatus = this.status.proximo();
        if (proximoStatus != this.status) {
            registrarHorarioStatus(proximoStatus);
            this.status = proximoStatus;
        }
    }

    /**
     * Atualiza para um status específico
     */
    public void atualizarStatus(StatusEntrega novoStatus) {
        registrarHorarioStatus(novoStatus);
        this.status = novoStatus;
    }

    /**
     * Registra o horário da mudança de status
     */
    private void registrarHorarioStatus(StatusEntrega status) {
        LocalDateTime agora = LocalDateTime.now();
        switch (status) {
            case COLETA -> this.dataHoraColeta = agora;
            case EM_PROCESSAMENTO -> this.dataHoraProcessamento = agora;
            case A_CAMINHO -> this.dataHoraACaminho = agora;
            case ENTREGUE -> this.dataHoraEntregue = agora;
        }
    }

    /**
     * Atribui um motorista à solicitação
     */
    public void atribuirMotorista(Motorista motorista) {
        if (motorista == null || !motorista.estaDisponivel()) {
            throw new IllegalArgumentException("Motorista inválido ou indisponível");
        }
        this.motorista = motorista;
    }

    /**
     * Verifica se a entrega foi finalizada
     */
    public boolean estaFinalizada() {
        return this.status.isFinalizado();
    }

    /**
     * Calcula o tempo decorrido desde a criação até a entrega
     */
    public Long calcularTempoTotalEntrega() {
        if (dataHoraColeta == null || dataHoraEntregue == null) {
            return null;
        }
        return java.time.Duration.between(dataHoraColeta, dataHoraEntregue).toMinutes();
    }
}
