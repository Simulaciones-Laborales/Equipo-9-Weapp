package com.tuempresa.creditflow.creditflow_api.controller;

import com.tuempresa.creditflow.creditflow_api.dto.ExtendedBaseResponse;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycFileUploadRequestDTO;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycStatusUpdateDTO;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycVerificationResponseDTO;
import com.tuempresa.creditflow.creditflow_api.enums.KycEntityType;
import com.tuempresa.creditflow.creditflow_api.service.impl.KycVerificationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para gestionar verificaciones KYC (Know Your Customer)
 * de usuarios y empresas.
 */
@Tag(name = "KYC Verification", description = "Gestión de verificaciones KYC para usuarios y empresas")
@RestController
@RequestMapping("/api/kyc")
public class KycVerificationController {

    private final KycVerificationServiceImpl kycService;

    public KycVerificationController(KycVerificationServiceImpl kycService) {
        this.kycService = kycService;
    }

    /**
     * Inicia una verificación KYC con carga de hasta tres documentos.
     *
     * @param entityId   ID del usuario o empresa a verificar
     * @param entityType Tipo de entidad (USER o COMPANY)
     * @param document1  Primer documento (opcional)
     * @param document2  Segundo documento (opcional)
     * @param document3  Tercer documento (opcional)
     * @return Información de la verificación creada
     */
    @Operation(summary = "Inicia verificación KYC con archivos",
            description = "Permite iniciar la verificación KYC de un usuario o empresa cargando hasta 3 documentos obligatorios.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificación creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = KycVerificationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error en los archivos enviados o datos inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping(value = "/start", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExtendedBaseResponse<KycVerificationResponseDTO>> startVerificationWithFiles(
            @RequestParam("entityId") UUID entityId,
            @RequestParam("entityType") KycEntityType entityType,
            @RequestPart(value = "document1", required = false) MultipartFile document1,
            @RequestPart(value = "document2", required = false) MultipartFile document2,
            @RequestPart(value = "document3", required = false) MultipartFile document3
    ) {
        KycFileUploadRequestDTO dto = new KycFileUploadRequestDTO(entityId, entityType, document1, document2, document3);
        return ResponseEntity.ok(kycService.startVerificationWithFiles(dto));
    }

    /**
     * Obtiene todas las verificaciones KYC registradas.
     *
     * @return Lista de todas las verificaciones
     */
    @Operation(summary = "Obtener todas las verificaciones KYC")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de verificaciones KYC",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = KycVerificationResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<ExtendedBaseResponse<List<KycVerificationResponseDTO>>> getAll() {
        return ResponseEntity.ok(kycService.getAll());
    }

    /**
     * Obtiene la verificación KYC por ID.
     *
     * @param id ID de la verificación
     * @return Información de la verificación
     */
    @Operation(summary = "Obtener verificación por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificación encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = KycVerificationResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró la verificación", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ExtendedBaseResponse<KycVerificationResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(kycService.getById(id));
    }

    /**
     * Actualiza el estado de una verificación KYC.
     *
     * @param id  ID de la verificación
     * @param dto DTO con la información del nuevo estado
     * @return Verificación actualizada
     */
    @Operation(summary = "Actualizar estado de verificación KYC")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = KycVerificationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Verificación no encontrada", content = @Content)
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<ExtendedBaseResponse<KycVerificationResponseDTO>> updateStatus(@PathVariable UUID id,
                                                                   @RequestBody KycStatusUpdateDTO dto) {
        return ResponseEntity.ok(kycService.updateStatus(id, dto));
    }

    /**
     * Elimina una verificación KYC por ID.
     *
     * @param id ID de la verificación
     * @return No content (204) si se elimina correctamente
     */
    @Operation(summary = "Eliminar verificación KYC")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Verificación eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Verificación no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        kycService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
