package com.tuempresa.creditflow.creditflow_api.controllers;

import com.tuempresa.creditflow.creditflow_api.dtos.*;
import com.tuempresa.creditflow.creditflow_api.service.KycVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/kyc")
public class KycVerificationController {

    private final KycVerificationService kycService;

    public KycVerificationController(KycVerificationService kycService) {
        this.kycService = kycService;
    }

    @PostMapping("/start")
    public ResponseEntity<KycVerificationResponseDTO> startVerification(@RequestBody KycVerificationRequestDTO dto) {
        return ResponseEntity.ok(kycService.startVerification(dto));
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