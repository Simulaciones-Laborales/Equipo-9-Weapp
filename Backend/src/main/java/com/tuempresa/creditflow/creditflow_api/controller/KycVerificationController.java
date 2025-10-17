package com.tuempresa.creditflow.creditflow_api.controller;

import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycFileUploadRequestDTO;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycStatusUpdateDTO;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycVerificationRequestDTO;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycVerificationResponseDTO;
import com.tuempresa.creditflow.creditflow_api.model.KycStatus;
import com.tuempresa.creditflow.creditflow_api.service.KycVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/kyc")
public class KycVerificationController {

    private final KycVerificationService kycService;

    public KycVerificationController(KycVerificationService kycService) {
        this.kycService = kycService;
    }

    @PostMapping(value = "/start", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<KycVerificationResponseDTO> startVerificationWithFiles(
            @RequestParam("userId") UUID userId, // ✅ CAMBIO AQUÍ
            @RequestPart(value = "selfie", required = false) MultipartFile selfie,
            @RequestPart(value = "dniFront", required = false) MultipartFile dniFront,
            @RequestPart(value = "dniBack", required = false) MultipartFile dniBack
    ) {
        KycFileUploadRequestDTO dto = new KycFileUploadRequestDTO(userId, selfie, dniFront, dniBack);
        return ResponseEntity.ok(kycService.startVerificationWithFiles(dto));
    }

    @GetMapping
    public ResponseEntity<List<KycVerificationResponseDTO>> getAll() {
        return ResponseEntity.ok(kycService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<KycVerificationResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(kycService.getById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<KycVerificationResponseDTO> updateStatus(@PathVariable UUID id, @RequestBody KycStatusUpdateDTO dto) {
        return ResponseEntity.ok(kycService.updateStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        kycService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
