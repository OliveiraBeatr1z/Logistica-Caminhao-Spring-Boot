package com.example.frota.motorista;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciar motoristas (interface web)
 */
@Controller
@RequestMapping("/motoristas")
public class MotoristaController {

    @Autowired
    private MotoristaService service;

    @GetMapping("/cadastrar")
    public String mostrarFormularioCadastro(Model model) {
        return "motorista/cadastrar";
    }

    @PostMapping("/cadastrar")
    public String cadastrar(@ModelAttribute @Valid DadosCadastroMotorista dados, Model model) {
        try {
            service.cadastrar(dados);
            model.addAttribute("mensagem", "Motorista cadastrado com sucesso!");
            return "redirect:/motoristas/mostrar";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            return "motorista/cadastrar";
        }
    }

    @GetMapping("/mostrar")
    public String mostrarTodos(Model model) {
        List<DadosListagemMotorista> motoristas = service.listarTodos();
        model.addAttribute("motoristas", motoristas);
        return "motorista/mostrar";
    }

    @GetMapping("/mostrar/{id}")
    public String mostrarDetalhes(@PathVariable Long id, Model model) {
        Motorista motorista = service.buscarPorId(id);
        model.addAttribute("motorista", motorista);
        return "motorista/detalhe";
    }

    @GetMapping("/atualizar/{id}")
    public String mostrarFormularioAtualizar(@PathVariable Long id, Model model) {
        Motorista motorista = service.buscarPorId(id);
        model.addAttribute("motorista", motorista);
        return "motorista/atualizar";
    }

    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Long id, @ModelAttribute DadosAtualizacaoMotorista dados) {
        service.atualizar(id, dados);
        return "redirect:/motoristas/mostrar/" + id;
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id) {
        service.deletar(id);
        return "redirect:/motoristas/mostrar";
    }

    @PostMapping("/inativar/{id}")
    public String inativar(@PathVariable Long id) {
        service.inativar(id);
        return "redirect:/motoristas/mostrar";
    }

    @GetMapping("/disponiveis")
    public String mostrarDisponiveis(Model model) {
        List<DadosListagemMotorista> motoristas = service.listarDisponiveis();
        model.addAttribute("motoristas", motoristas);
        model.addAttribute("titulo", "Motoristas Disponíveis");
        return "motorista/mostrar";
    }
}

