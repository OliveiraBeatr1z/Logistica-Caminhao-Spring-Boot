package com.example.frota.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller para servir a página estática FrotaLux (SPA)
 */
@Controller
public class FrotaluxController {

    /**
     * Rota principal que serve o HTML estático (SPA)
     */
    @GetMapping({"/", "/frotalux", "/app"})
    public String frotaluxSPA() {
        return "forward:/frotalux.html";
    }
}
