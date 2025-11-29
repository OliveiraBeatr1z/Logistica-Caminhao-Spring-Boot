package com.example.frota.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller para o Dashboard principal
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping
    public String mostrarDashboard() {
        // Por enquanto, apenas renderiza o template
        // Em produção, aqui buscaríamos dados do backend e adicionaríamos ao model
        return "dashboard";
    }
}

