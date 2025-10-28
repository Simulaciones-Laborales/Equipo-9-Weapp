package com.tuempresa.creditflow.creditflow_api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuempresa.creditflow.creditflow_api.dto.ExtendedBaseResponse;
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
import com.tuempresa.creditflow.creditflow_api.utils.AuthenticationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
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
    private final IUserService userService; // Necesario para buscar la entidad User
    private final AuthenticationUtils authenticationUtils; // Necesario para obtener el principal

    // ------------------------------------------------------------------
    // MÉTODO DE AYUDA PARA OBTENER EL OBJETO USER (SUSTITUTO DE authenticationUtils.getAuthenticatedUser())
    // ------------------------------------------------------------------
    /**
     * Obtiene la entidad User completa a partir del principal en el contexto de seguridad.
     * @return El objeto User completo.
     * @throws RuntimeException (o una excepción custom) si el usuario no está autenticado o no se encuentra.
     */
    private User getAuthenticatedUser() {
        // Obtiene el principal (email/username)
        String principal = authenticationUtils.getLoggedInPrincipal();

        if (principal == null) {
            throw new RuntimeException("Usuario no autenticado o principal no encontrado.");
        }
        return userService.findEntityByPrincipal(principal);
    }

    // ------------------------------------------------------------------
    // RESTO DE ENDPOINTS CON LA LLAMADA AL NUEVO MÉTODO CORREGIDA
    // ------------------------------------------------------------------

    @Operation(
            summary = "Listar todas las solicitudes de crédito (Operador)",
            description = "Permite a los usuarios con rol OPERADOR obtener una lista paginada de todas las solicitudes de crédito en el sistema. Se puede filtrar por estado (status)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista paginada de todas las solicitudes obtenida correctamente.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class, subTypes = CreditApplicationResponseDTO.class))
            ),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (usuario sin rol OPERADOR).",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<Page<CreditApplicationResponseDTO>> getAllApplications(
            @Parameter(description = "Estado de la solicitud para filtrar los resultados (opcional).")
            @RequestParam(value = "status", required = false) CreditStatus status,
            @ParameterObject Pageable pageable
    ) {
        Page<CreditApplicationResponseDTO> applicationsPage = creditApplicationService.getAllCreditApplications(status, pageable);
        return ResponseEntity.ok(applicationsPage);
    }

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
            @Parameter(
                    description = "JSON con los datos de la solicitud. Debe enviarse como string.",
                    schema = @Schema(implementation = CreditApplicationRequestDTO.class)
            )
            @RequestPart("data") String data,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws JsonProcessingException {
        User currentUser = getAuthenticatedUser();

        ObjectMapper mapper = new ObjectMapper();
        CreditApplicationRequestDTO dto = mapper.readValue(data, CreditApplicationRequestDTO.class);

        CreditApplicationResponseDTO created = creditApplicationService
                .createApplicationWithFiles(dto, files, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Actualizar una solicitud de crédito existente con nuevos datos o documentos",
            description = """
        Permite modificar una solicitud de crédito existente.
        El cuerpo de la solicitud debe enviarse como multipart/form-data,
        incluyendo un campo `data` (JSON con los datos a actualizar)
        y opcionalmente archivos en el campo `documents`.
        Ejemplo de campo `data`:
        {
            "amount": 75000,
            "operatorComments": "Se adjuntan nuevos balances"
        }
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Solicitud de crédito actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = CreditApplicationResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada", content = @Content)
    })

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CreditApplicationResponseDTO> updateApplication(
            @Parameter(description = "ID (UUID) de la solicitud a actualizar", required = true)
            @PathVariable UUID id,

            @Parameter(
                    description = "JSON con los datos a actualizar. Debe enviarse como string.",
                    schema = @Schema(implementation = CreditApplicationUpdateRequestDTO.class)
            )
            @RequestPart("data") String data,

            @Parameter(description = "Archivos PDF o documentos adicionales a adjuntar.")
            @RequestPart(value = "documents", required = false) List<MultipartFile> documents
    ) throws JsonProcessingException {
        User currentUser = getAuthenticatedUser();

        List<MultipartFile> safeDocuments = (documents != null) ? documents : List.of();

        ObjectMapper mapper = new ObjectMapper();
        CreditApplicationUpdateRequestDTO dto = mapper.readValue(data, CreditApplicationUpdateRequestDTO.class);

        CreditApplicationResponseDTO updated = creditApplicationService
                .updateApplication(id, dto, safeDocuments, currentUser);

        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Obtener una solicitud de crédito por ID",
            description = """
        Permite obtener los detalles de una solicitud de crédito específica por su ID.
        Solo el propietario de la solicitud puede acceder a esta información.
        """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud de crédito obtenida correctamente",
                    content = @Content(schema = @Schema(implementation = CreditApplicationResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Solicitud de crédito no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (usuario no autorizado)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/owner")
    public ResponseEntity<CreditApplicationResponseDTO> getByIdAndOwner(
            @Parameter(description = "ID (UUID) de la solicitud a consultar.")
            @PathVariable UUID id) {
        User currentUser = getAuthenticatedUser();
        CreditApplicationResponseDTO dto = creditApplicationService.getApplicationByIdAndUser(id, currentUser);
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "Obtener una solicitud de crédito por ID",
            description = """
        Permite obtener los detalles de una solicitud de crédito específica por su ID.
        Solo el propietario de la solicitud, un operador o un administrador puede acceder a esta información.
        """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud de crédito obtenida correctamente",
                    content = @Content(schema = @Schema(implementation = CreditApplicationResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Solicitud de crédito no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (usuario no autorizado)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(path = "/{id}")
    public ResponseEntity<CreditApplicationResponseDTO> getById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(creditApplicationService.getById(id));
    }

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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de solicitudes obtenida correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CreditApplicationResponseDTO.class)))),
            @ApiResponse(responseCode = "403", description = "Usuario no autenticado o acceso denegado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping()
    public ResponseEntity<List<CreditApplicationResponseDTO>> getMyCreditApplications(
            @Parameter(description = "Estado de la solicitud para filtrar los resultados (opcional).")
            @RequestParam(value = "status", required = false) CreditStatus status) {

        User currentUser = getAuthenticatedUser();
        List<CreditApplicationResponseDTO> applications = creditApplicationService.getCreditApplicationsByUser(currentUser, status);
        return ResponseEntity.ok(applications);
    }

    @Operation(
            summary = "Listar solicitudes de crédito por empresa",
            description = """
        Permite obtener todas las solicitudes de crédito asociadas a una empresa específica.
        Solo el propietario de la empresa o un usuario con rol ADMIN/OPERADOR puede acceder a esta información.
        """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de solicitudes obtenida correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CreditApplicationResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Empresa no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (no es propietario ni ADMIN/OPERADOR)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<CreditApplicationResponseDTO>> getByCompany(
            @Parameter(description = "ID (UUID) de la empresa cuyas solicitudes se desean consultar.")
            @PathVariable UUID companyId) {
        User currentUser = getAuthenticatedUser();
        List<CreditApplicationResponseDTO> list = creditApplicationService.getApplicationsByCompany(companyId, currentUser);
        return ResponseEntity.ok(list);
    }

    @Operation(
            summary = "Cambiar el estado de una solicitud de crédito",
            description = """
        Permite cambiar el estado de una solicitud de crédito existente.
        Solo el propietario de la empresa asociada a la solicitud o un usuario con rol ADMIN/OPERADOR puede realizar esta acción.
        Se pueden agregar comentarios opcionales del operador.
        """,
            security = @SecurityRequirement(name = "bearerAuth") // si usas JWT o similar
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud de crédito actualizada correctamente",
                    content = @Content(schema = @Schema(implementation = CreditApplicationResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Solicitud de crédito no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto al cambiar el estado (permiso denegado o estado no válido)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}/status")
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

        // CORREGIDO: Usar el nuevo método auxiliar
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
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(description = "Objeto de página de Spring Data con registros de historial.",
                                            implementation = Page.class,
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

        // CORREGIDO: Usar el nuevo método auxiliar
        User currentUser = getAuthenticatedUser();
        Page<CreditApplicationHistoryDTO> page = creditApplicationHistoryService.getHistoryByApplication(id, currentUser, pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Eliminar TODOS los pdfs e imágenes (limpieza completa) en cloudinary")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todos los contenidos eliminados exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error al eliminar los contenidos")
    })
    @DeleteMapping("/risk-document/purge-docs")
    public ResponseEntity<ExtendedBaseResponse<Void>> purgeAllContents() {
        return ResponseEntity.ok(creditApplicationService.purgeAllImageCloudinary());
    }
}