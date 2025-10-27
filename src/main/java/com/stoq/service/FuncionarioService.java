package com.stoq.service;

import com.stoq.domain.Funcionario;
import com.stoq.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * Serviço responsável por fazer validações de funcionario para as permissões do sistema
 */
@Service
public class FuncionarioService {

    private final FuncionarioRepository repository;

    public FuncionarioService(FuncionarioRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Funcionario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Funcionário não encontrado com ID: " + id));
    }
}