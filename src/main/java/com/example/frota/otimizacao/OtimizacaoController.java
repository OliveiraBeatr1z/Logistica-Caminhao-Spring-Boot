package com.example.frota.otimizacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para otimização de carga
 */
@Controller
@RequestMapping("/otimizacao")
public class OtimizacaoController {

    @Autowired
    private OtimizacaoCargaService service;

    @GetMapping
    public String mostrarDashboard(Model model) {
        // Mostra sugestões de agrupamento
        List<OtimizacaoCargaService.GrupoEntrega> grupos = service.sugerirAgrupamentoOtimo();
        model.addAttribute("grupos", grupos);
        
        // Mostra solicitações agrupadas por região
        var porRegiao = service.agruparPorRegiao();
        model.addAttribute("porRegiao", porRegiao);
        
        return "otimizacao/dashboard";
    }

    @GetMapping("/sugerir")
    public String sugerirCaminhao(@RequestParam List<Long> solicitacaoIds, Model model) {
        try {
            OtimizacaoCargaService.SugestaoCaminhao sugestao = 
                service.sugerirCaminhaoOtimizado(solicitacaoIds);
            model.addAttribute("sugestao", sugestao);
            return "otimizacao/sugestao";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            return "redirect:/otimizacao";
        }
    }
}

