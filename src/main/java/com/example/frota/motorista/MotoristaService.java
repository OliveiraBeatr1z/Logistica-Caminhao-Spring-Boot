package com.example.frota.motorista;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para gerenciar motoristas
 */
@Service
public class MotoristaService {

    @Autowired
    private MotoristaRepository repository;

    @Transactional
    public Motorista cadastrar(DadosCadastroMotorista dados) {
        // Validar se CPF ou CNH já existem
        if (repository.findByCpf(dados.cpf()) != null) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
        if (repository.findByCnh(dados.cnh()) != null) {
            throw new IllegalArgumentException("CNH já cadastrada");
        }

        Motorista motorista = new Motorista(dados);
        return repository.save(motorista);
    }

    public List<DadosListagemMotorista> listarTodos() {
        return repository.findAll().stream()
                .map(DadosListagemMotorista::new)
                .collect(Collectors.toList());
    }

    public List<DadosListagemMotorista> listarAtivos() {
        return repository.findByAtivoTrue().stream()
                .map(DadosListagemMotorista::new)
                .collect(Collectors.toList());
    }

    public List<DadosListagemMotorista> listarDisponiveis() {
        return repository.findMotoristasDisponiveis(LocalDate.now()).stream()
                .map(DadosListagemMotorista::new)
                .collect(Collectors.toList());
    }

    public Motorista buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Motorista não encontrado"));
    }

    @Transactional
    public Motorista atualizar(Long id, DadosAtualizacaoMotorista dados) {
        Motorista motorista = buscarPorId(id);
        motorista.atualizarInformacoes(dados);
        return repository.save(motorista);
    }

    @Transactional
    public void deletar(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void inativar(Long id) {
        Motorista motorista = buscarPorId(id);
        motorista.setAtivo(false);
        repository.save(motorista);
    }

    /**
     * Atualiza a localização do motorista (chamado pelo app do motorista)
     */
    @Transactional
    public void atualizarLocalizacao(Long motoristaId, double latitude, double longitude) {
        Motorista motorista = buscarPorId(motoristaId);
        motorista.atualizarLocalizacao(latitude, longitude);
        repository.save(motorista);
    }

    /**
     * Lista motoristas com CNH vencendo nos próximos 30 dias
     */
    public List<DadosListagemMotorista> listarCnhVencendo() {
        LocalDate hoje = LocalDate.now();
        LocalDate daqui30Dias = hoje.plusDays(30);
        return repository.findMotoristasComCnhVencendo(hoje, daqui30Dias).stream()
                .map(DadosListagemMotorista::new)
                .collect(Collectors.toList());
    }
}

