package com.example.frota.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrotaluxController {

    @GetMapping({"/frotalux", "/frotalux.html"})
    public String frotalux() {
        // Redireciona para o arquivo estático em /static/frotalux.html
        return "forward:/frotalux.html";
    }
}

