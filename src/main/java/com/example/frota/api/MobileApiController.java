package com.example.frota.api;

import com.example.frota.enums.StatusEntrega;
import com.example.frota.motorista.MotoristaService;
import com.example.frota.solicitacaoTransporte.SolicitacaoTransporte;
import com.example.frota.solicitacaoTransporte.SolicitacaoTransporteRepository;
import com.example.frota.avaliacao.AvaliacaoService;
import com.example.frota.avaliacao.DadosAvaliacaoCliente;
import com.example.frota.avaliacao.DadosAvaliacaoRecebedor;
import com.example.frota.pagamento.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * API REST para integração com app do motorista e sistema mobile
 */
@RestController
@RequestMapping("/api")
public class MobileApiController {

    @Autowired
    private MotoristaService motoristaService;

    @Autowired
    private SolicitacaoTransporteRepository solicitacaoRepository;

    @Autowired
    private AvaliacaoService avaliacaoService;

    @Autowired
    private PagamentoService pagamentoService;

    /**
     * Atualiza localização do motorista (enviada pelo app)
     */
    @PostMapping("/motorista/{id}/localizacao")
    public ResponseEntity<?> atualizarLocalizacao(
            @PathVariable Long id,
            @RequestBody LocalizacaoDTO localizacao) {
        try {
            motoristaService.atualizarLocalizacao(id, localizacao.latitude(), localizacao.longitude());
            return ResponseEntity.ok().body(new MensagemResponse("Localização atualizada"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErroResponse(e.getMessage()));
        }
    }

    /**
     * Lista entregas do motorista
     */
    @GetMapping("/motorista/{id}/entregas")
    public ResponseEntity<?> listarEntregasMotorista(@PathVariable Long id) {
        List<SolicitacaoTransporte> entregas = solicitacaoRepository.findAll().stream()
                .filter(s -> s.getMotorista() != null && s.getMotorista().getId().equals(id))
                .filter(s -> !s.estaFinalizada())
                .toList();

        return ResponseEntity.ok(entregas);
    }

    /**
     * Atualiza status da entrega (chamado pelo app do motorista)
     */
    @PostMapping("/entrega/{id}/status")
    public ResponseEntity<?> atualizarStatusEntrega(
            @PathVariable Long id,
            @RequestBody AtualizacaoStatusDTO dados) {
        try {
            SolicitacaoTransporte solicitacao = solicitacaoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));

            solicitacao.atualizarStatus(dados.novoStatus());
            solicitacaoRepository.save(solicitacao);

            return ResponseEntity.ok().body(new MensagemResponse("Status atualizado para: " + dados.novoStatus().getNome()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErroResponse(e.getMessage()));
        }
    }

    /**
     * Marca entrega como concluída
     */
    @PostMapping("/entrega/{id}/finalizar")
    public ResponseEntity<?> finalizarEntrega(@PathVariable Long id) {
        try {
            SolicitacaoTransporte solicitacao = solicitacaoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));

            solicitacao.atualizarStatus(StatusEntrega.ENTREGUE);
            solicitacaoRepository.save(solicitacao);

            return ResponseEntity.ok().body(new MensagemResponse("Entrega finalizada com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErroResponse(e.getMessage()));
        }
    }

    /**
     * Lista todas as avaliações
     */
    @GetMapping("/avaliacao")
    public ResponseEntity<?> listarAvaliacoes() {
        return ResponseEntity.ok(avaliacaoService.listarTodas());
    }

    /**
     * Busca avaliação por ID
     */
    @GetMapping("/avaliacao/{id}")
    public ResponseEntity<?> buscarAvaliacao(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(avaliacaoService.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Busca avaliação de uma solicitação específica
     */
    @GetMapping("/avaliacao/solicitacao/{solicitacaoId}")
    public ResponseEntity<?> buscarAvaliacaoPorSolicitacao(@PathVariable Long solicitacaoId) {
        var avaliacao = avaliacaoService.buscarPorSolicitacao(solicitacaoId);
        if (avaliacao.isPresent()) {
            return ResponseEntity.ok(avaliacao.get());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Cliente avalia a entrega
     */
    @PostMapping("/avaliacao/cliente")
    public ResponseEntity<?> avaliarComoCliente(@RequestBody @Valid DadosAvaliacaoCliente dados) {
        try {
            avaliacaoService.avaliarPorCliente(dados);
            return ResponseEntity.ok().body(new MensagemResponse("Avaliação registrada com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErroResponse(e.getMessage()));
        }
    }

    /**
     * Recebedor avalia a entrega
     */
    @PostMapping("/avaliacao/recebedor")
    public ResponseEntity<?> avaliarComoRecebedor(@RequestBody @Valid DadosAvaliacaoRecebedor dados) {
        try {
            avaliacaoService.avaliarPorRecebedor(dados);
            return ResponseEntity.ok().body(new MensagemResponse("Avaliação registrada com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErroResponse(e.getMessage()));
        }
    }

    /**
     * Consulta status do pagamento
     */
    @GetMapping("/pagamento/solicitacao/{solicitacaoId}")
    public ResponseEntity<?> consultarPagamento(@PathVariable Long solicitacaoId) {
        var pagamento = pagamentoService.buscarPorSolicitacao(solicitacaoId);
        if (pagamento.isPresent()) {
            return ResponseEntity.ok(pagamento.get());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Confirma pagamento (webhook simulado)
     */
    @PostMapping("/pagamento/{id}/confirmar")
    public ResponseEntity<?> confirmarPagamento(
            @PathVariable Long id,
            @RequestBody ConfirmacaoPagamentoDTO dados) {
        try {
            pagamentoService.confirmar(id, dados.transacaoId());
            return ResponseEntity.ok().body(new MensagemResponse("Pagamento confirmado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErroResponse(e.getMessage()));
        }
    }

    // DTOs
    public record LocalizacaoDTO(double latitude, double longitude) {}
    public record AtualizacaoStatusDTO(StatusEntrega novoStatus) {}
    public record ConfirmacaoPagamentoDTO(String transacaoId) {}
    public record MensagemResponse(String mensagem) {}
    public record ErroResponse(String erro) {}
}

