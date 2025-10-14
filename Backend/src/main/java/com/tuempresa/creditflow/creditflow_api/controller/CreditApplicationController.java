package com.tuempresa.creditflow.creditflow_api.controller;

import com.tuempresa.creditflow.creditflow_api.dto.history.CreditApplicationHistoryDTO;
import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationRequestDTO;
import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationResponseDTO;
import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationStatusChangeDTO;
import com.tuempresa.creditflow.creditflow_api.model.User;
import com.tuempresa.creditflow.creditflow_api.service.CreditApplicationHistoryService;
import com.tuempresa.creditflow.creditflow_api.service.CreditApplicationService;
import com.tuempresa.creditflow.creditflow_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/credit-applications")
@RequiredArgsConstructor
@Validated
public class CreditApplicationController {

    private final CreditApplicationService creditApplicationService;
    private final CreditApplicationHistoryService creditApplicationHistoryService;
    private final UserService userService;

    // -------------------------
    // Helper: obtener usuario autenticado desde SecurityContext
    // -------------------------
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findEntityByEmail(email);}

    // -------------------------
    // Create a new credit application
    // POST /api/credit-applications
    // -------------------------
    @PostMapping
    public ResponseEntity<CreditApplicationResponseDTO> createApplication(
            @RequestBody @Valid CreditApplicationRequestDTO dto) {

        User currentUser = getAuthenticatedUser();
        CreditApplicationResponseDTO created = creditApplicationService.createApplication(dto, currentUser);
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
    @PutMapping("/{id}")
    public ResponseEntity<CreditApplicationResponseDTO> updateApplication(
            @PathVariable UUID id,
            @RequestBody @Valid CreditApplicationRequestDTO dto) {

        User currentUser = getAuthenticatedUser();
        CreditApplicationResponseDTO updated = creditApplicationService.updateApplication(id, dto, currentUser);
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

