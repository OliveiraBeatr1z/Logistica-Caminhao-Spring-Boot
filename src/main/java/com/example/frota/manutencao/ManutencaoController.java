package com.example.frota.manutencao;

import com.example.frota.enums.TipoManutencao;
import com.example.frota.caminhao.CaminhaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciar manutenções
 */
@Controller
@RequestMapping("/manutencoes")
public class ManutencaoController {

    @Autowired
    private ManutencaoService service;

    @Autowired
    private CaminhaoService caminhaoService;

    @GetMapping("/cadastrar")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("caminhoes", caminhaoService.procurarTodos());
        model.addAttribute("tiposManutencao", TipoManutencao.values());
        return "manutencao/cadastrar";
    }

    @PostMapping("/cadastrar")
    public String cadastrar(@ModelAttribute @Valid DadosCadastroManutencao dados, Model model) {
        service.cadastrar(dados);
        model.addAttribute("mensagem", "Manutenção cadastrada com sucesso!");
        return "redirect:/manutencoes/mostrar";
    }

    @GetMapping("/mostrar")
    public String mostrarTodas(Model model) {
        List<DadosListagemManutencao> manutencoes = service.listarTodas();
        model.addAttribute("manutencoes", manutencoes);
        return "manutencao/mostrar";
    }

    @GetMapping("/mostrar/{id}")
    public String mostrarDetalhes(@PathVariable Long id, Model model) {
        Manutencao manutencao = service.buscarPorId(id);
        model.addAttribute("manutencao", manutencao);
        return "manutencao/detalhe";
    }

    @GetMapping("/caminhao/{caminhaoId}")
    public String mostrarPorCaminhao(@PathVariable Long caminhaoId, Model model) {
        List<DadosListagemManutencao> manutencoes = service.listarPorCaminhao(caminhaoId);
        model.addAttribute("manutencoes", manutencoes);
        return "manutencao/mostrar";
    }

    @GetMapping("/alertas")
    public String mostrarAlertas(Model model) {
        List<ManutencaoService.AlertaManutencao> alertas = service.verificarTodosAlertas();
        model.addAttribute("alertas", alertas);
        return "manutencao/alertas";
    }

    @GetMapping("/alertas/{caminhaoId}")
    public String mostrarAlertasCaminhao(@PathVariable Long caminhaoId, Model model) {
        List<ManutencaoService.AlertaManutencao> alertas = service.verificarAlertas(caminhaoId);
        model.addAttribute("alertas", alertas);
        model.addAttribute("caminhaoId", caminhaoId);
        return "manutencao/alertas";
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id) {
        service.deletar(id);
        return "redirect:/manutencoes/mostrar";
    }
}

