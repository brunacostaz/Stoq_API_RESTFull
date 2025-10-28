package com.stoq.service;

import com.stoq.domain.*;
import com.stoq.repository.ConsultaRepository;
import com.stoq.repository.MaterialRepository;
import com.stoq.repository.PresetMaterialRepository;
import com.stoq.repository.QRCodeRepository;
import com.stoq.dto.QRCodeDTO.QRCodeResponseDTO;
import com.stoq.mapper.QRCodeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class QRCodeService {

    private final QRCodeRepository qrCodeRepository;
    private final ConsultaRepository consultaRepository;
    private final PresetMaterialRepository presetMaterialRepository;
    private final MaterialRepository materialRepository;
    private final EstoqueService estoqueService;
    private final FuncionarioService funcionarioService;

    public QRCodeService(QRCodeRepository qrCodeRepository,
                         ConsultaRepository consultaRepository,
                         PresetMaterialRepository presetMaterialRepository,
                         MaterialRepository materialRepository,
                         EstoqueService estoqueService,
                         FuncionarioService funcionarioService) {
        this.qrCodeRepository = qrCodeRepository;
        this.consultaRepository = consultaRepository;
        this.presetMaterialRepository = presetMaterialRepository;
        this.materialRepository = materialRepository;
        this.estoqueService = estoqueService;
        this.funcionarioService = funcionarioService;
    }

    /**
     * Enfermeiro gera QRCode para a consulta selecionada.
     */
    @Transactional
    public QRCodeResponseDTO gerarQRCode(Long consultaId, Long enfermeiroId) {
        Funcionario enfermeiro = funcionarioService.buscarPorId(enfermeiroId);

        if (!"ENFERMEIRO".equalsIgnoreCase(enfermeiro.getCargo())) {
            throw new IllegalArgumentException("Acesso negado: apenas ENFERMEIROS podem gerar QRCode.");
        }

        Consultas consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new NoSuchElementException("Consulta não encontrada."));

        // Cria o objeto QRCode (Entity)
        QRCode qr = new QRCode();
        qr.setIdConsulta(consulta.getIdConsulta());
        qr.setIdEnfermeiro(enfermeiro.getIdFuncionario());
        qr.setIdAdminValidador(0L);
        qr.setIdLaboratorio(consulta.getIdLab());
        qr.setCodigo("QR-" + System.currentTimeMillis());
        qr.setStatus("PENDENTE");
        qr.setDtGeracao(LocalDate.now());
        qr.setDtValidacao(LocalDate.now());

        qrCodeRepository.save(qr);
        System.out.println("QRCODE gerado com sucesso! ID: " + qr.getIdQRCode());

        return QRCodeMapper.toResponseDTO(qr);
    }


    /**
     * Admin valida QRCode → retira materiais do estoque.
     */
    @Transactional
    public void validarQRCode(Long qrCodeId, Long adminId) {
        Funcionario admin = funcionarioService.buscarPorId(adminId);

        if (!"ADMIN".equalsIgnoreCase(admin.getCargo())) {
            throw new IllegalArgumentException("Acesso negado: apenas ADMIN pode validar QRCode.");
        }

        QRCode qr = qrCodeRepository.findById(qrCodeId)
                .orElseThrow(() -> new NoSuchElementException("QRCode não encontrado."));

        // 1. Atualiza status do QR Code
        qr.setStatus("ACEITO");
        qr.setIdAdminValidador(admin.getIdFuncionario());
        qr.setDtValidacao(LocalDate.now());
        qrCodeRepository.save(qr);

        System.out.println("QRCODE validado com sucesso! Iniciando baixa no estoque.");

        // 2. Busca o Preset e Materiais
        Consultas consulta = consultaRepository.findById(qr.getIdConsulta())
                .orElseThrow(() -> new NoSuchElementException("Consulta associada ao QRCode não encontrada."));

        List<PresetMaterial> materiaisRetirados = presetMaterialRepository.findByIdIdPreset(consulta.getIdPreset());

        if (materiaisRetirados.isEmpty()) {
            System.out.println("Aviso: Nenhum material configurado neste Preset.");
            return;
        }

        // 3. Chama o serviço de estoque para dar baixa nos itens
        List<PedidoItens> itensParaRetirada = materiaisRetirados.stream().map(pm ->
                new PedidoItens(
                        null, // idPedido
                        pm.getId().getIdMaterial(), // idMaterial
                        pm.getQtdePorExame(), // qtdeSolicitada (qtde a ser retirada)
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        "RETIRADA_QR",
                        LocalDate.now()
                )
        ).collect(Collectors.toList());

        estoqueService.retiradaEstoque(
                qr.getIdLaboratorio(),
                qr.getIdEnfermeiro(),
                qrCodeId,
                itensParaRetirada
        );

        System.out.println("Baixa no estoque concluída com sucesso.");
    }

    /**
     * Lista materiais do preset associado ao QRCode (para admin visualizar)
     */
    @Transactional(readOnly = true)
    public List<Material> listarMateriaisDoQRCode(Long qrCodeId) {
        QRCode qr = qrCodeRepository.findById(qrCodeId)
                .orElseThrow(() -> new NoSuchElementException("QRCode não encontrado."));

        Consultas consulta = consultaRepository.findById(qr.getIdConsulta())
                .orElseThrow(() -> new NoSuchElementException("Consulta associada ao QRCode não encontrada."));

        List<Long> materiaisIds = presetMaterialRepository.findMaterialIdsByPresetId(consulta.getIdPreset());

        return materialRepository.findAllById(materiaisIds);
    }

    /**
     * Busca um QR Code pelo ID e mapeia para DTO. (Novo método para GET)
     */
    @Transactional(readOnly = true)
    public QRCodeResponseDTO buscarPorId(Long qrCodeId) {
        QRCode qrCode = qrCodeRepository.findById(qrCodeId)
                .orElseThrow(() -> new NoSuchElementException("QR Code " + qrCodeId + " não encontrado."));

        return QRCodeMapper.toResponseDTO(qrCode);
    }

    /**
     * Listar todos os QRCodes cadastrados
     */
    @Transactional(readOnly = true)
    public List<QRCodeResponseDTO> listarTodos() {
        return qrCodeRepository.findAll().stream()
                .map(QRCodeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}