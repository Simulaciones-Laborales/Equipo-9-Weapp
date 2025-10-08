package com.tuempresa.creditflow.creditflow_api.controller;

import com.tuempresa.creditflow.creditflow_api.dtos.company.CompanyRequestDTO;
import com.tuempresa.creditflow.creditflow_api.dtos.company.CompanyResponseDTO;
import com.tuempresa.creditflow.creditflow_api.model.User;
import com.tuempresa.creditflow.creditflow_api.service.CompanyService;
import com.tuempresa.creditflow.creditflow_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    /*private final CompanyService companyService;
    private final UserService userService; // Para obtener el usuario autenticado

    // ✅ Crear una nueva empresa
    @PostMapping
    public ResponseEntity<CompanyResponseDTO> createCompany(@RequestBody CompanyRequestDTO companyRequestDTO) {
        User currentUser = getAuthenticatedUser();
        CompanyResponseDTO createdCompany = companyService.createCompany(companyRequestDTO, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCompany);
    }

    // ✅ Obtener todas las empresas del usuario autenticado
    @GetMapping
    public ResponseEntity<List<CompanyResponseDTO>> getAllCompanies() {
        User currentUser = getAuthenticatedUser();
        List<CompanyResponseDTO> companies = companyService.getCompaniesByUser(currentUser);
        return ResponseEntity.ok(companies);
    }

    // ✅ Obtener una empresa por su ID (solo si pertenece al usuario)
    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> getCompanyById(@PathVariable UUID id) {
        User currentUser = getAuthenticatedUser();
        CompanyResponseDTO company = companyService.getCompanyByIdAndUser(id, currentUser);
        return ResponseEntity.ok(company);
    }

    // ✅ Actualizar una empresa existente
    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> updateCompany(
            @PathVariable UUID id,
            @RequestBody CompanyRequestDTO companyRequestDTO) {

        User currentUser = getAuthenticatedUser();
        CompanyResponseDTO updatedCompany = companyService.updateCompany(id, companyRequestDTO, currentUser);
        return ResponseEntity.ok(updatedCompany);
    }

    // ✅ Eliminar una empresa por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable UUID id) {
        User currentUser = getAuthenticatedUser();
        companyService.deleteCompany(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    // -----------------------------
    // Método auxiliar para obtener el usuario autenticado
    // -----------------------------
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // username/email
        return userService.findByEmail(email);
    }*/
}

