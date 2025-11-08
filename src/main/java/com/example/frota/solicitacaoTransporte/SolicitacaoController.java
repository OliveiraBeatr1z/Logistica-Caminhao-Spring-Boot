package com.example.frota.solicitacaoTransporte;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.frota.caixaPadronizada.CaixaPadronizadaService;
import com.example.frota.caminhao.CaminhaoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/solicitacoes")
public class SolicitacaoController {

    @Autowired
    private FreightService freightService;
    
    @Autowired
    private SolicitacaoTransporteRepository repository;
    
    @Autowired
    private SolicitacaoTransporteService solicitacaoService;
    
    @Autowired
    private CaixaPadronizadaService caixaService;
    
    @Autowired
    private CaminhaoService caminhaoService;

    @GetMapping("/cadastrar")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("caixas", caixaService.getAllCaixas());
        model.addAttribute("caminhoes", caminhaoService.procurarTodos());
        return "solicitacao/cadastrar";
    }

    @PostMapping("/cadastrar")
    public String cadastrarSolicitacao(@ModelAttribute @Valid DadosCadastroSolicitacao dados, Model model) {
        SolicitacaoTransporte solicitacao = solicitacaoService.cadastrar(dados);
        
        model.addAttribute("mensagem", "Solicitação cadastrada com sucesso!");
        model.addAttribute("valorFrete", solicitacao.getValorFrete());
        return "redirect:/solicitacoes/mostrar/" + solicitacao.getId();
    }

    @GetMapping("/mostrar")
    public String mostrarSolicitacoes(Model model) {
        List<SolicitacaoTransporte> solicitacoes = repository.findAll();
        model.addAttribute("solicitacoes", solicitacoes);        
        return "solicitacao/mostrar";
    }
    
    @GetMapping("/mostrar/{id}")
    public String mostrarSolicitacaoPorId(@PathVariable("id") Long id, Model model) {
        try {
            SolicitacaoTransporte solicitacao = solicitacaoService.buscarPorId(id);
            FreightService.DetalheFrete detalhe = solicitacaoService.calcularFreteDetalhado(id);
            
            model.addAttribute("solicitacao", solicitacao);
            model.addAttribute("detalhe", detalhe);
            
            // Buscar informações da caixa e caminhão se existirem
            if (solicitacao.getCaixaId() != null) {
                var caixa = caixaService.procurarPorId(solicitacao.getCaixaId());
                if (caixa != null) {
                    model.addAttribute("caixa", caixa);
                }
            }
            if (solicitacao.getCaminhaoId() != null) {
                var caminhao = caminhaoService.procurarPorId(solicitacao.getCaminhaoId());
                if (caminhao.isPresent()) {
                    model.addAttribute("caminhao", caminhao.get());
                }
            }
            
            return "solicitacao/mostrar-detalhe";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Solicitação não encontrada: " + e.getMessage());
            return "redirect:/solicitacoes/mostrar";
        }
    }

    @PostMapping("/deletar/{id}")
    public String deleteById(@PathVariable("id") Long id) {
        solicitacaoService.deleteBySolicitacaoId(id);
        return "redirect:/solicitacoes/mostrar";
    }

    @DeleteMapping("/deletar/{id}")
    public String deletarSolicitacao(@PathVariable("id") Long id) {
        solicitacaoService.deleteBySolicitacaoId(id);
        return "redirect:/solicitacoes/mostrar";
    }

    @GetMapping("/atualizar/{id}")
    public String mostrarFormularioAtualizar(@PathVariable("id") Long id, Model model) {
        SolicitacaoTransporte solicitacao = repository.findById(id).orElse(null);
        model.addAttribute("solicitacao", solicitacao);
        model.addAttribute("caixas", caixaService.getAllCaixas());
        model.addAttribute("caminhoes", caminhaoService.procurarTodos());
        return "solicitacao/atualizar";
    }

    @PostMapping("/atualizar/{id}")
    public String atualizarSolicitacao(@PathVariable("id") Long id,
                                @ModelAttribute DadosAtualizacaoSolicitacao dados) {
        solicitacaoService.atualizarSolicitacao(id, dados);
        return "redirect:/solicitacoes/mostrar/" + id;
    }
}

// API REST para cotação e criação
@RestController
@RequestMapping("/api/solicitacoes")
class SolicitacaoApiController {

    private final FreightService freightService;
    private final SolicitacaoTransporteService service;

    public SolicitacaoApiController(
            FreightService freightService, 
            SolicitacaoTransporteService service) {
        this.freightService = freightService;
        this.service = service;
    }

    @PostMapping("/cotacao")
    public ResponseEntity<?> cotar(@RequestBody DadosCadastroSolicitacao dados) {
        SolicitacaoTransporte s = new SolicitacaoTransporte(dados);
        double valor = freightService.calcularFretePorSolicitacao(s);
        FreightService.DetalheFrete detalhe = freightService.calcularFreteDetalhado(s);
        return ResponseEntity.ok().body(new CotacaoResponse(valor, detalhe));
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody DadosCadastroSolicitacao dados) {
        SolicitacaoTransporte solicitacao = service.cadastrar(dados);
        FreightService.DetalheFrete detalhe = freightService.calcularFreteDetalhado(solicitacao);
        return ResponseEntity.ok().body(new CriacaoResponse(solicitacao.getId(), solicitacao.getValorFrete(), detalhe));
    }

    record CotacaoResponse(double valor, FreightService.DetalheFrete detalhe) {}
    record CriacaoResponse(Long id, double valor, FreightService.DetalheFrete detalhe) {}
}

