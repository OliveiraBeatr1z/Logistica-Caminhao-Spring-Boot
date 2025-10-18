package com.example.frota.solicitacaoTransporte;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/solicitacoes")
public class SolicitacaoController {

    private final FreightService freightService;
    private final SolicitacaoTransporteRepository repo;

    public SolicitacaoController(FreightService freightService, SolicitacaoTransporteRepository repo) {
        this.freightService = freightService;
        this.repo = repo;
    }

    @PostMapping("/cotacao")
    public ResponseEntity<?> cotar(@RequestBody SolicitacaoTransporte s) {
        double valor = freightService.calcularFretePorSolicitacao(s);
        return ResponseEntity.ok().body(new CotacaoResponse(valor));
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody SolicitacaoTransporte s) {
        double valor = freightService.calcularFretePorSolicitacao(s);
        s.setId(null); // ensure new
        SolicitacaoTransporte salvo = repo.save(s);
        return ResponseEntity.ok().body(new CriacaoResponse(salvo.getId(), valor));
    }

    record CotacaoResponse(double valor) {}
    record CriacaoResponse(Long id, double valor) {}
}
