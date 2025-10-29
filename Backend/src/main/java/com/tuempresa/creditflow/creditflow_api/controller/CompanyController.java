package com.tuempresa.creditflow.creditflow_api.controller;

import com.tuempresa.creditflow.creditflow_api.dto.company.CompanyRequestDTO;
import com.tuempresa.creditflow.creditflow_api.dto.company.CompanyResponseDTO;
import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationRequestDTO;
import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationResponseDTO;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycVerificationRequestCompanyDTO;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycVerificationRequestDTO;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycVerificationResponseDTO;
import com.tuempresa.creditflow.creditflow_api.model.User;
import com.tuempresa.creditflow.creditflow_api.service.CompanyService;
import com.tuempresa.creditflow.creditflow_api.service.CreditApplicationService;
import com.tuempresa.creditflow.creditflow_api.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

// Anotaciones de Swagger/OpenAPI
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.List;
import java.util.UUID;

// ----------------------------------------------------------------------
// ANOTACIONES A NIVEL DE CLASE
// ----------------------------------------------------------------------

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(name = "Empresas", description = "Gestión de los registros de empresas solicitantes de crédito. Las operaciones están restringidas al usuario autenticado.")
public class CompanyController {

    private final CompanyService companyService;
    private final CreditApplicationService creditApplicationService;
    private final IUserService userService; // Para obtener el usuario autenticado


    // ------------------------------------------------------------------
    // ENDPOINT 1: CREAR EMPRESA (POST)
    // ------------------------------------------------------------------
    @Operation(
        summary = "Crear nueva empresa",
        description = "Registra una nueva empresa asociada al usuario autenticado. El usuario actual será registrado como el creador.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Empresa creada exitosamente.",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CompanyResponseDTO.class)
                )
            ),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej. ID Fiscal duplicado, campos faltantes)."),
            @ApiResponse(responseCode = "401", description = "No autenticado o Token inválido.")
        }
    )
    @PostMapping
    public ResponseEntity<CompanyResponseDTO> createCompany(
        @RequestBody CompanyRequestDTO companyRequestDTO) {
        
        User currentUser = getAuthenticatedUser();
        CompanyResponseDTO createdCompany = companyService.createCompany(companyRequestDTO, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCompany);
    }

    // ------------------------------------------------------------------
    // ENDPOINT 2: OBTENER TODAS LAS EMPRESAS DEL USUARIO (GET /)
    // ------------------------------------------------------------------
    @Operation(
        summary = "Listar empresas del usuario",
        description = "Devuelve una lista de todas las empresas asociadas al usuario autenticado.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de empresas recuperada.",
                content = @Content(
                    mediaType = "application/json",
                    // Uso de ArraySchema para indicar que devuelve una lista del DTO
                    array = @ArraySchema(schema = @Schema(implementation = CompanyResponseDTO.class))
                )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado.")
        }
    )
    @GetMapping
    public ResponseEntity<List<CompanyResponseDTO>> getAllCompanies() {
        User currentUser = getAuthenticatedUser();
        List<CompanyResponseDTO> companies = companyService.getCompaniesByUser(currentUser);
        return ResponseEntity.ok(companies);
    }

    // ------------------------------------------------------------------
    // ENDPOINT 3: OBTENER EMPRESA POR ID (GET /{id})
    // ------------------------------------------------------------------
    @Operation(
        summary = "Obtener empresa por ID",
        description = "Consulta una empresa específica. La empresa solo se devuelve si pertenece al usuario autenticado.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Empresa encontrada y devuelta.",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CompanyResponseDTO.class)
                )
            ),
            @ApiResponse(responseCode = "404", description = "Empresa no encontrada o el ID no pertenece al usuario."),
            @ApiResponse(responseCode = "401", description = "No autenticado.")
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> getCompanyById(
        @Parameter(description = "Identificador único (UUID) de la empresa a consultar.")
        @PathVariable UUID id) {
        
        User currentUser = getAuthenticatedUser();
        CompanyResponseDTO company = companyService.getCompanyByIdAndUser(id, currentUser);
        return ResponseEntity.ok(company);
    }

    // ------------------------------------------------------------------
    // ENDPOINT 4: ACTUALIZAR EMPRESA (PUT /{id})
    // ------------------------------------------------------------------
    @Operation(
        summary = "Actualizar empresa",
        description = "Actualiza la información de una empresa existente. Solo se permite actualizar las empresas del usuario autenticado.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Empresa actualizada exitosamente.",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CompanyResponseDTO.class)
                )
            ),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos."),
            @ApiResponse(responseCode = "404", description = "Empresa no encontrada o no pertenece al usuario."),
            @ApiResponse(responseCode = "401", description = "No autenticado.")
        }
    )
    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> updateCompany(
            @Parameter(description = "Identificador único (UUID) de la empresa a actualizar.")
            @PathVariable UUID id,
            @RequestBody CompanyRequestDTO companyRequestDTO) {

        User currentUser = getAuthenticatedUser();
        CompanyResponseDTO updatedCompany = companyService.updateCompany(id, companyRequestDTO, currentUser);
        return ResponseEntity.ok(updatedCompany);
    }

    // ------------------------------------------------------------------
    // ENDPOINT 5: ELIMINAR EMPRESA (DELETE /{id})
    // ------------------------------------------------------------------
    @Operation(
        summary = "Eliminar empresa",
        description = "Elimina una empresa por su ID. La empresa solo será eliminada si pertenece al usuario autenticado. Retorna una respuesta 204 sin contenido.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Empresa eliminada exitosamente."),
            @ApiResponse(responseCode = "404", description = "Empresa no encontrada o no pertenece al usuario."),
            @ApiResponse(responseCode = "401", description = "No autenticado.")
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(
        @Parameter(description = "Identificador único (UUID) de la empresa a eliminar.")
        @PathVariable UUID id) {
        
        User currentUser = getAuthenticatedUser();
        companyService.deleteCompany(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Listar solicitudes de crédito por empresa",
            description = "Devuelve una lista de las solicitudes de crédito de una empresa específica. Solo si la empresa pertenece al usuario autenticado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de solicitudes de crédito recuperada.",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = CreditApplicationRequestDTO.class))
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Empresa no encontrada o el ID no pertenece al usuario."),
                    @ApiResponse(responseCode = "401", description = "No autenticado.")
            }
    )
    @GetMapping("/{id}/credit-applications")
    public ResponseEntity<List<CreditApplicationResponseDTO>> getCreditRequestsByCompanyId(
            @Parameter(description = "Identificador único (UUID) de la empresa para la que se consultan las solicitudes.")
            @PathVariable("id") UUID companyId) {

        User currentUser = getAuthenticatedUser();
        List<CreditApplicationResponseDTO> creditRequests = creditApplicationService.getCreditApplicationsByCompanyIdAndUser(companyId, currentUser);

        return ResponseEntity.ok(creditRequests);
    }

    @Operation(
            summary = "Obtener KYC de la empresa",
            description = "Consulta la información de KYC (Know Your Customer) de una empresa específica. Solo si la empresa pertenece al usuario autenticado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "KYC de la empresa encontrado y devuelto.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = KycVerificationRequestCompanyDTO.class) // Asumiendo este DTO
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Empresa o registro de KYC no encontrado, o la empresa no pertenece al usuario."),
                    @ApiResponse(responseCode = "401", description = "No autenticado.")
            }
    )
    @GetMapping("/{id}/kyc")
    public ResponseEntity<KycVerificationResponseDTO> getCompanyKycById(
            @Parameter(description = "Identificador único (UUID) de la empresa cuyo KYC se va a consultar.")
            @PathVariable("id") UUID companyId) {

        User currentUser = getAuthenticatedUser();

        // El CompanyService manejará:
        // 1. Verificar que la empresa con `companyId` existe y pertenece a `currentUser`.
        // 2. Obtener la información de KYC asociada a esa empresa.
        KycVerificationResponseDTO kycResponse = companyService.getCompanyKycByIdAndUser(companyId, currentUser);

        return ResponseEntity.ok(kycResponse);
    }

    // -----------------------------
    // Método auxiliar (sin anotaciones de Swagger, ya que no es un endpoint)
    // -----------------------------
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("No se encontró ningún usuario autenticado en el contexto de seguridad");
        }
        String principal = authentication.getName();
        return userService.findEntityByPrincipal(principal);
    }
}
