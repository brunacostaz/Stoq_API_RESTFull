package com.stoq.controller;

import com.stoq.dto.QRCodeDTO.QRCodeGeracaoRequestDTO;
import com.stoq.dto.QRCodeDTO.QRCodeResponseDTO;
import com.stoq.service.QRCodeService;
import io.swagger.v3.oas.annotations.Operation; // Adicionado
import io.swagger.v3.oas.annotations.tags.Tag; // Adicionado
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.stoq.domain.Material;
import com.stoq.dto.QRCodeDTO.QRCodeValidacaoRequestDTO;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/qrcode")
@Tag(name = "QR Code", description = "Geração e Validação de QR Codes para Saída de Estoque") // Adicionado
public class QRCodeController {

    private final QRCodeService qrCodeService;

    public QRCodeController(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    /**
     * Endpoint para Enfermeiro gerar o QR Code de uma consulta.
     */
    @Operation(summary = "Gera um novo QR Code PENDENTE para uma consulta específica (Ação do Enfermeiro)") // Adicionado
    @PostMapping("/gerar")
    public ResponseEntity<?> gerarQRCode(@RequestBody QRCodeGeracaoRequestDTO request) {
        try {
            QRCodeResponseDTO qrCode = qrCodeService.gerarQRCode(
                    request.getConsultaId(),
                    request.getEnfermeiroId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(qrCode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao gerar QR Code: " + e.getMessage());
        }
    }

    /**
     * Endpoint para Admin validar o QR Code e dar baixa no estoque.
     */
    @Operation(summary = "Valida o QR Code, muda o status para ACEITO e aciona a baixa no Estoque (Ação do Admin)") // Adicionado
    @PutMapping("/validar/{qrCodeId}")
    public ResponseEntity<String> validarQRCode(
            @PathVariable Long qrCodeId,
            @RequestBody QRCodeValidacaoRequestDTO request) {
        try {
            qrCodeService.validarQRCode(qrCodeId, request.getAdminId());
            return ResponseEntity.ok("QR Code validado e estoque baixado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno na validação e baixa de estoque: " + e.getMessage());
        }
    }

    /**
     * Endpoint para buscar um QR Code por ID.
     */
    @Operation(summary = "Busca o QR Code por ID") // Adicionado
    @GetMapping("/{qrCodeId}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long qrCodeId) {
        try {
            QRCodeResponseDTO qrCode = qrCodeService.buscarPorId(qrCodeId);
            return ResponseEntity.ok(qrCode);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Endpoint para listar materiais do preset associado a um QR Code.
     */
    @Operation(summary = "Lista os materiais necessários para o QR Code (Consulta o Preset associado)") // Adicionado
    @GetMapping("/materiais/{qrCodeId}")
    public ResponseEntity<?> listarMateriaisDoQRCode(@PathVariable Long qrCodeId) {
        try {
            List<Material> materiais = qrCodeService.listarMateriaisDoQRCode(qrCodeId);
            return ResponseEntity.ok(materiais);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar materiais: " + e.getMessage());
        }
    }
}