package com.tuempresa.creditflow.creditflow_api.controller;

import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationUpdateRequestDTO;
import com.tuempresa.creditflow.creditflow_api.dto.history.CreditApplicationHistoryDTO;
import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationRequestDTO;
import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationResponseDTO;
import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationStatusChangeDTO;
import com.tuempresa.creditflow.creditflow_api.enums.CreditStatus;
import com.tuempresa.creditflow.creditflow_api.model.User;
import com.tuempresa.creditflow.creditflow_api.service.CreditApplicationHistoryService;
import com.tuempresa.creditflow.creditflow_api.service.CreditApplicationService;
import com.tuempresa.creditflow.creditflow_api.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/credit-applications")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CreditApplicationController {

    private final CreditApplicationService creditApplicationService;
    private final CreditApplicationHistoryService creditApplicationHistoryService;
    private final IUserService userService;

    // -------------------------
    // Helper: obtener usuario autenticado desde SecurityContext
    // -------------------------
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findEntityByEmail(email);}

    @Operation(summary = "Crear una nueva solicitud de crédito con documentos",
            description = """
           Permite crear una solicitud de crédito para la empresa del usuario autenticado.
           Se pueden enviar archivos que serán utilizados para calcular el puntaje de riesgo.
           """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Solicitud de crédito creada exitosamente",
                    content = @Content(schema = @Schema(implementation = CreditApplicationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CreditApplicationResponseDTO> createApplicationWithFiles(
            @RequestPart("data") @Valid CreditApplicationRequestDTO dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        User currentUser = getAuthenticatedUser();

        CreditApplicationResponseDTO created = creditApplicationService
                .createApplicationWithFiles(dto, files, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }



    // -------------------------
    // Get application by ID (only if user has access)
    // GET /api/credit-applications/{id}
    // -------------------------
    @GetMapping("/{id}")
    public ResponseEntity<CreditApplicationResponseDTO> getById(@PathVariable UUID id) {
        User currentUser = getAuthenticatedUser();
        CreditApplicationResponseDTO dto = creditApplicationService.getApplicationByIdAndUser(id, currentUser);
        return ResponseEntity.ok(dto);
    }

    // -------------------------
    // Get all credit applications of the authenticated user
    // GET /api/credit-applications/my
    // -------------------------
    @GetMapping("/my")
    public ResponseEntity<List<CreditApplicationResponseDTO>> getMyCreditApplications(@RequestParam(value = "status", required = false) CreditStatus status
    ) {
        User currentUser = getAuthenticatedUser();
        List<CreditApplicationResponseDTO> applications = creditApplicationService.getCreditApplicationsByUser(currentUser, status);
        return ResponseEntity.ok(applications);
    }

    // -------------------------
    // List applications by company (owner or operator/admin)
    // GET /api/credit-applications/company/{companyId}
    // -------------------------
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<CreditApplicationResponseDTO>> getByCompany(@PathVariable UUID companyId) {
        User currentUser = getAuthenticatedUser();
        List<CreditApplicationResponseDTO> list = creditApplicationService.getApplicationsByCompany(companyId, currentUser);
        return ResponseEntity.ok(list);
    }

    // -------------------------
    // Update an application (amount / operatorComments) — business rules apply inside service
    // PUT /api/credit-applications/{id}
    // -------------------------
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CreditApplicationResponseDTO> updateApplication(
            @PathVariable UUID id,
            @RequestPart("data") @Valid CreditApplicationUpdateRequestDTO dto,
            @RequestPart(value = "documents", required = false) List<MultipartFile> documents
    ) {
        User currentUser = getAuthenticatedUser();

        List<MultipartFile> safeDocuments = (documents != null) ? documents : List.of();

        CreditApplicationResponseDTO updated = creditApplicationService
                .updateApplication(id, dto, safeDocuments, currentUser);

        return ResponseEntity.ok(updated);
    }




    // -------------------------
    // Change status of an application (creates history entry)
    // POST /api/credit-applications/{id}/status
    // -------------------------
    @PostMapping("/{id}/status")
    public ResponseEntity<CreditApplicationResponseDTO> changeStatus(
            @PathVariable UUID id,
            @RequestBody @Valid CreditApplicationStatusChangeDTO dto) {

        User currentUser = getAuthenticatedUser();
        CreditApplicationResponseDTO updated = creditApplicationService.changeStatus(id, dto, currentUser);
        return ResponseEntity.ok(updated);
    }

    // -------------------------
    // Delete an application
    // DELETE /api/credit-applications/{id}
    // -------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable UUID id) {
        User currentUser = getAuthenticatedUser();
        creditApplicationService.deleteApplication(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    // -------------------------
    // Get paginated history for an application
    // GET /api/credit-applications/{id}/history?page=0&size=20&sort=createdAt,desc
    // -------------------------
    @GetMapping("/{id}/history")
    public ResponseEntity<Page<CreditApplicationHistoryDTO>> getHistory(
            @PathVariable UUID id,
            Pageable pageable) {

        User currentUser = getAuthenticatedUser();
        Page<CreditApplicationHistoryDTO> page = creditApplicationHistoryService.getHistoryByApplication(id, currentUser, pageable);
        return ResponseEntity.ok(page);
    }
}

