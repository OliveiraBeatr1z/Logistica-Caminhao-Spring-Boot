package com.example.frota.caixaPadronizada;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/caixas")
public class CaixaPadronizadaController {

    @Autowired
    private CaixaPadronizadaRepository repository;

    @Autowired
    private CaixaPadronizadaService caixaService;

    @GetMapping("/cadastrar")
    public String mostrarFormularioCadastro(Model model) {
        return "caixa/cadastrar";
    }

    @PostMapping("/cadastrar")
    public String cadastrarCaixa(@ModelAttribute @Valid DadosCadastroCaixa dados, Model model) {
        CaixaPadronizada caixa = new CaixaPadronizada(dados);
        repository.save(caixa);
        
        model.addAttribute("mensagem", "Caixa cadastrada com sucesso!");
        return "redirect:/caixas/mostrar";
    }

    @GetMapping("/mostrar")
    public String mostrarCaixas(Model model) {
        List<CaixaPadronizada> caixas = repository.findAll();
        model.addAttribute("caixas", caixas);        
        return "caixa/mostrar";
    }
    
    @GetMapping("/mostrar/{id}")
    public String mostrarCaixaPorId(@PathVariable("id") Long id, Model model) {
        CaixaPadronizada caixa = repository.findById(id).orElse(null);
        model.addAttribute("caixa", caixa);
        return "caixa/mostrar-detalhe";
    }

    @PostMapping("/deletar/{id}")
    public String deleteById(@PathVariable("id") Long id) {
        caixaService.deleteByCaixaId(id);
        return "redirect:/caixas/mostrar";
    }

    @DeleteMapping("/deletar/{id}")
    public String deletarCaixa(@PathVariable("id") Long id) {
        caixaService.deleteByCaixaId(id);
        return "redirect:/caixas/mostrar";
    }

    @GetMapping("/atualizar/{id}")
    public String mostrarFormularioAtualizar(@PathVariable("id") Long id, Model model) {
        CaixaPadronizada caixa = repository.findById(id).orElse(null);
        model.addAttribute("caixa", caixa);
        return "caixa/atualizar";
    }

    @PostMapping("/atualizar/{id}")
    public String atualizarCaixa(@PathVariable("id") Long id,
                                @ModelAttribute DadosAtualizacaoCaixa dados) {
        caixaService.atualizarCaixa(id, dados);
        return "redirect:/caixas/mostrar";
    }
}

