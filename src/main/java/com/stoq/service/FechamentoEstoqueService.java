package com.stoq.service;

import com.stoq.repository.EstoqueRepository;
import com.stoq.repository.HistoricoEstoqueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class FechamentoEstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final HistoricoEstoqueRepository historicoRepository;

    public FechamentoEstoqueService(EstoqueRepository estoqueRepository, HistoricoEstoqueRepository historicoRepository) {
        this.estoqueRepository = estoqueRepository;
        this.historicoRepository = historicoRepository;
    }

    /**
     * Fecha o estoque do dia:
     * Copia o saldo atual do ESTOQUE para o HISTORICO_ESTOQUE e limpa registros antigos no ESTOQUE.
     */
    @Transactional
    public void fecharDia(LocalDate dia) {
        historicoRepository.copiarEstoqueParaHistorico(dia);

        estoqueRepository.limparDiasAntigos(dia);
    }
}