package com.tuempresa.creditflow.creditflow_api.controller;

import com.tuempresa.creditflow.creditflow_api.dto.history.CreditApplicationHistoryDTO;
import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationRequestDTO;
import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationResponseDTO;
import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationStatusChangeDTO;
import com.tuempresa.creditflow.creditflow_api.model.CreditStatus;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

// ----------------------------------------------------------------------
// ANOTACIONES A NIVEL DE CLASE
// ----------------------------------------------------------------------

@RestController
@RequestMapping("/api/credit-applications")
@RequiredArgsConstructor
@Validated
@Tag(name = "Solicitudes de Crédito", description = "Gestión completa del ciclo de vida de las solicitudes de crédito, incluyendo creación, actualización de datos y cambio de estados.")
public class CreditApplicationController {

    private final CreditApplicationService creditApplicationService;
    private final CreditApplicationHistoryService creditApplicationHistoryService;
    private final UserService userService;

    // Obtener el usuario autenticado (se excluye de la documentación de Swagger)
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findEntityByEmail(email);}

    // ------------------------------------------------------------------
    // ENDPOINT 1: CREAR SOLICITUD (POST /)
    // ------------------------------------------------------------------
    @Operation(
        summary = "Crear nueva solicitud de crédito",
        description = "Inicia el proceso de solicitud de crédito para una empresa existente. La solicitud se asocia al usuario autenticado.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Solicitud creada exitosamente. El estado inicial es PENDING.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditApplicationResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o empresa no válida."),
            @ApiResponse(responseCode = "401", description = "No autenticado.")
        }
    )
    @PostMapping
    public ResponseEntity<CreditApplicationResponseDTO> createApplication(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos necesarios para crear la solicitud (ID de la empresa y monto).",
                required = true,
                content = @Content(schema = @Schema(implementation = CreditApplicationRequestDTO.class))
            )
            @RequestBody @Valid CreditApplicationRequestDTO dto) {

        User currentUser = getAuthenticatedUser();
        CreditApplicationResponseDTO created = creditApplicationService.createApplication(dto, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ------------------------------------------------------------------
    // ENDPOINT 2: OBTENER POR ID (GET /{id})
    // ------------------------------------------------------------------
    @Operation(
        summary = "Obtener solicitud por ID",
        description = "Consulta los detalles de una solicitud de crédito específica. Solo accesible si la solicitud pertenece al usuario.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Solicitud encontrada.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditApplicationResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada o no pertenece al usuario."),
            @ApiResponse(responseCode = "401", description = "No autenticado.")
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<CreditApplicationResponseDTO> getById(
        @Parameter(description = "ID (UUID) de la solicitud a consultar.")
        @PathVariable UUID id) {
        
        User currentUser = getAuthenticatedUser();
        CreditApplicationResponseDTO dto = creditApplicationService.getApplicationByIdAndUser(id, currentUser);
        return ResponseEntity.ok(dto);
    }

    // ------------------------------------------------------------------
    // ENDPOINT 3: OBTENER POR EMPRESA (GET /company/{companyId})
    // ------------------------------------------------------------------
    @Operation(
        summary = "Obtener solicitudes por empresa",
        description = "Devuelve todas las solicitudes de crédito asociadas a una empresa específica. La empresa debe pertenecer al usuario autenticado.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de solicitudes encontradas para la empresa.",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CreditApplicationResponseDTO.class))
                )
            ),
            @ApiResponse(responseCode = "404", description = "Empresa no encontrada o no pertenece al usuario."),
            @ApiResponse(responseCode = "401", description = "No autenticado.")
        }
    )
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
    public ResponseEntity<List<CreditApplicationResponseDTO>> getByCompany(
        @Parameter(description = "ID (UUID) de la empresa cuyas solicitudes se desean consultar.")
        @PathVariable UUID companyId) {
        
        User currentUser = getAuthenticatedUser();
        List<CreditApplicationResponseDTO> list = creditApplicationService.getApplicationsByCompany(companyId, currentUser);
        return ResponseEntity.ok(list);
    }

    // ------------------------------------------------------------------
    // ENDPOINT 4: ACTUALIZAR SOLICITUD (PUT /{id})
    // ------------------------------------------------------------------
    @Operation(
        summary = "Actualizar monto y comentarios",
        description = "Permite la actualización del monto solicitado y los comentarios internos de una solicitud existente.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Solicitud actualizada con el nuevo monto y/o comentarios.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditApplicationResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos."),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada o no pertenece al usuario."),
            @ApiResponse(responseCode = "401", description = "No autenticado.")
        }
    )
    @PutMapping("/{id}")
    public ResponseEntity<CreditApplicationResponseDTO> updateApplication(
            @Parameter(description = "ID (UUID) de la solicitud a actualizar.")
            @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Nuevos datos para el monto y comentarios.",
                required = true,
                content = @Content(schema = @Schema(implementation = CreditApplicationRequestDTO.class))
            )
            @RequestBody @Valid CreditApplicationRequestDTO dto) {

        User currentUser = getAuthenticatedUser();
        CreditApplicationResponseDTO updated = creditApplicationService.updateApplication(id, dto, currentUser);
        return ResponseEntity.ok(updated);
    }

    // ------------------------------------------------------------------
    // ENDPOINT 5: CAMBIAR ESTADO (POST /{id}/status)
    // ------------------------------------------------------------------
    @Operation(
        summary = "Cambiar estado de la solicitud",
        description = "Transiciona el estado de la solicitud (ej. a APPROVED, REJECTED, UNDER_REVIEW). La acción genera un registro en el historial.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Estado de la solicitud cambiado exitosamente. Retorna la solicitud actualizada.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditApplicationResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "El nuevo estado no es válido o la transición es ilegal."),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada."),
            @ApiResponse(responseCode = "401", description = "No autenticado.")
        }
    )
    @PostMapping("/{id}/status")
    public ResponseEntity<CreditApplicationResponseDTO> changeStatus(
            @Parameter(description = "ID (UUID) de la solicitud cuyo estado se va a cambiar.")
            @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "El nuevo estado (e.g., APPROVED, REJECTED) y comentarios opcionales.",
                required = true,
                content = @Content(schema = @Schema(implementation = CreditApplicationStatusChangeDTO.class))
            )
            @RequestBody @Valid CreditApplicationStatusChangeDTO dto) {

        User currentUser = getAuthenticatedUser();
        CreditApplicationResponseDTO updated = creditApplicationService.changeStatus(id, dto, currentUser);
        return ResponseEntity.ok(updated);
    }

    // ------------------------------------------------------------------
    // ENDPOINT 6: ELIMINAR SOLICITUD (DELETE /{id})
    // ------------------------------------------------------------------
    @Operation(
        summary = "Eliminar solicitud",
        description = "Elimina permanentemente una solicitud de crédito. Solo permitido si el estado lo permite y pertenece al usuario. Retorna 204 No Content.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Solicitud eliminada exitosamente."),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada o no pertenece al usuario."),
            @ApiResponse(responseCode = "401", description = "No autenticado.")
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(
        @Parameter(description = "ID (UUID) de la solicitud a eliminar.")
        @PathVariable UUID id) {
        
        User currentUser = getAuthenticatedUser();
        creditApplicationService.deleteApplication(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    // ------------------------------------------------------------------
    // ENDPOINT 7: OBTENER HISTORIAL (GET /{id}/history)
    // ------------------------------------------------------------------
    @Operation(
        summary = "Consultar historial de la solicitud",
        description = "Obtiene el registro de auditoría y eventos (cambios de estado, comentarios, actualizaciones) para una solicitud específica. La respuesta está paginada.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Página de registros de historial recuperada.",
                // Para Page<T>, se suele usar un wrapper o simplemente documentar que el contenido es un array, 
                // pero SpringDoc lo maneja automáticamente para objetos Page.
                content = @Content(mediaType = "application/json", 
                                   schema = @Schema(description = "Objeto de página de Spring Data con registros de historial.", 
                                                    implementation = Page.class, 
                                                    // Es una técnica común especificar el tipo de contenido de la página
                                                    // aunque el esquema de Page de Spring sea complejo
                                                    subTypes = CreditApplicationHistoryDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada."),
            @ApiResponse(responseCode = "401", description = "No autenticado.")
        }
    )
    @GetMapping("/{id}/history")
    public ResponseEntity<Page<CreditApplicationHistoryDTO>> getHistory(
            @Parameter(description = "ID (UUID) de la solicitud cuyo historial se desea consultar.")
            @PathVariable UUID id,
            @Parameter(description = "Parámetros de paginación (ej. page=0&size=10&sort=createdAt,desc).")
            Pageable pageable) {

        User currentUser = getAuthenticatedUser();
        Page<CreditApplicationHistoryDTO> page = creditApplicationHistoryService.getHistoryByApplication(id, currentUser, pageable);
        return ResponseEntity.ok(page);
    }
}
