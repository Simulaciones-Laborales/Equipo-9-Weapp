package com.tuempresa.creditflow.creditflow_api.controller;

import com.tuempresa.creditflow.creditflow_api.dto.ExtendedBaseResponse;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycFileUploadRequestDTO;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycStatusUpdateDTO;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycVerificationResponseDTO;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycVerifiedCompanyResponseDTO;
import com.tuempresa.creditflow.creditflow_api.enums.KycEntityType;
import com.tuempresa.creditflow.creditflow_api.service.IKycVerificationService;
import com.tuempresa.creditflow.creditflow_api.service.impl.KycVerificationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping("/api/kyc")
public class KycVerificationController {

    private final IKycVerificationService kycService;

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
            description = "Permite iniciar la verificación KYC de un usuario o empresa cargando hasta 3 documentos obligatorios, incluyendo la **consulta crediticia en la Central de Deudores del BCRA**.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificación creada exitosamente. Incluye el resumen BCRA.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = KycVerificationResponseDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Respuesta con Deuda Grave",
                                            summary = "Ejemplo de respuesta de verificación para un USER o COMPANY con riesgo crediticio (Situación 3 o superior).",
                                            value = """
                                                {
                                                  "status": 200,
                                                  "message": "Verificación KYC iniciada correctamente",
                                                  "data": {
                                                    "idKyc": "007668d5-9fa1-4aab-9b86-deb4c6f49940",
                                                    "status": "PENDING",
                                                    "kycEntityType": "USER",
                                                    "entityName": "Juan Pérez",
                                                    "document1Url": "https://cloudinary.com/doc1...",
                                                    "bcraSummary": {
                                                      "isConsulted": true,
                                                      "hasSeriousDebt": true,
                                                      "hasUnpaidCheques": false,
                                                      "worstSituation": "SITUACION 4: Con Alto Riesgo de Insolvencia",
                                                      "currentDebts": [
                                                        {
                                                          "entityName": "BANCO X",
                                                          "situationCode": 4,
                                                          "situationDesc": "SITUACION 4: Con Alto Riesgo de Insolvencia",
                                                          "debtAmount": 1500.50,
                                                          "daysOverdue": 120,
                                                          "isJudicialProcess": false
                                                        }
                                                      ]
                                                    }
                                                  }
                                                }
                                                """
                                    ),
                                    @ExampleObject(
                                            name = "Respuesta Normal Sin Deudas",
                                            summary = "Ejemplo de respuesta de verificación para una entidad sin deudas o con deuda en Situación 1 (Normal).",
                                            value = """
                                                {
                                                  "status": 200,
                                                  "message": "Verificación KYC iniciada correctamente",
                                                  "data": {
                                                    "idKyc": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
                                                    "status": "PENDING",
                                                    "kycEntityType": "COMPANY",
                                                    "entityName": "MiEmpresa S.A.",
                                                    "document1Url": "https://cloudinary.com/doc1...",
                                                    "bcraSummary": {
                                                      "isConsulted": true,
                                                      "hasSeriousDebt": false,
                                                      "hasUnpaidCheques": false,
                                                      "worstSituation": "SITUACION 1: Normal (Riesgo Bajo)",
                                                      "currentDebts": [
                                                         {
                                                          "entityName": "BANCO Z",
                                                          "situationCode": 1,
                                                          "situationDesc": "SITUACION 1: Normal (Riesgo Bajo)",
                                                          "debtAmount": 0.0,
                                                          "daysOverdue": 0,
                                                          "isJudicialProcess": false
                                                         }
                                                      ]
                                                    }
                                                  }
                                                }
                                                """
                                    )
                            }
                    )),
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
    @GetMapping("/all")
    public ResponseEntity<ExtendedBaseResponse<List<KycVerificationResponseDTO>>> getAll() {
        return ResponseEntity.ok(kycService.getAll());
    }


    /**
     * Obtiene las verificaciones KYC registradas, con opción de filtrar por tipo de entidad y/o estado.
     *
     * @param kycEntityType Tipo de entidad KYC para filtrar (opcional)
     * @param status Estado de la verificación KYC para filtrar (opcional)
     * @return Lista de verificaciones KYC filtradas o todas si no se proporcionan filtros
     */
    @Operation(summary = "Obtener verificaciones KYC con filtros opcionales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de verificaciones KYC",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = KycVerificationResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping()
    public ResponseEntity<ExtendedBaseResponse<List<KycVerificationResponseDTO>>> getFiltered(
            @Parameter(description = "Tipo de entidad KYC (por ejemplo, 'PYME', 'COMPANY') para filtrar", required = false)
            @RequestParam(required = false) String kycEntityType,

            @Parameter(description = "Estado de la verificación KYC (por ejemplo, 'PENDING', 'VERIFIED', 'REJECTED') para filtrar", required = false)
            @RequestParam(required = false) String status) {

        // Aquí invocarías un método en tu servicio que maneje el filtrado.
        // Asumo que tu servicio tiene un método como 'getFiltered' o similar:
        return ResponseEntity.ok(kycService.getFiltered(kycEntityType, status));
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
     * Obtiene todas las empresas con verificaciones KYC registradas.
     *
     * @return Lista de todas las empresas con verificación registrada
     */
    @Operation(summary = "Obtener todas las empresas con verificación KYC")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de empresas con verificación KYC",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExtendedBaseResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/companies")
    public ResponseEntity<ExtendedBaseResponse<List<KycVerifiedCompanyResponseDTO>>> getVerifiedCompaniesDetails() {

        return ResponseEntity.ok(kycService.getVerifiedCompaniesDetails());
    }

    /**
     * Obtiene el estado KYC (Know Your Customer) de una empresa específica por su ID.
     *
     * @param companyId ID de la empresa a consultar.
     * @return El estado de verificación KYC de la empresa, o un error 404 si no se encuentra.
     */
    @Operation(
            summary = "Obtener el estado KYC de una empresa por ID",
            description = "Consulta la verificación KYC de una empresa específica usando su ID. Devuelve los detalles de la verificación."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado de verificación de empresa encontrado exitosamente.",
                    content = @Content(mediaType = "application/json",
                            // Usamos el DTO individual esperado para una respuesta por ID
                            schema = @Schema(implementation = KycVerifiedCompanyResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empresa o verificación KYC no encontrada.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor.",
                    content = @Content
            )
    })
    @GetMapping("/companies/{id}")
// 💡 CAMBIO 1: El PathVariable ahora se llama 'companyId' para mayor claridad
// 💡 CAMBIO 2: El retorno del servicio debe ser un DTO individual, no una lista.
    public ResponseEntity<ExtendedBaseResponse<KycVerificationResponseDTO>> getVerifiedCompanyStatusById(
            @PathVariable("id") UUID companyId) {

        // 💡 CAMBIO 3: Llama al método del servicio, que ahora debe retornar un DTO simple.
        ExtendedBaseResponse<KycVerificationResponseDTO> response = kycService.getCompanyStatusById(companyId);

        return ResponseEntity.ok(response);
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
