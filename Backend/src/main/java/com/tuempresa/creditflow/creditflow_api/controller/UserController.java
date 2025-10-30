package com.tuempresa.creditflow.creditflow_api.controller;

import com.tuempresa.creditflow.creditflow_api.dto.ExtendedBaseResponse;
import com.tuempresa.creditflow.creditflow_api.dto.company.CompanyResponseDTO;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycVerificationResponseDTO;
import com.tuempresa.creditflow.creditflow_api.dto.user.*;
import com.tuempresa.creditflow.creditflow_api.enums.KycStatus;
import com.tuempresa.creditflow.creditflow_api.service.CreditApplicationService;
import com.tuempresa.creditflow.creditflow_api.service.impl.KycVerificationServiceImpl;
import com.tuempresa.creditflow.creditflow_api.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Users", description = "Gestionar todos los End-Points de usuarios. (RESTRINGIDO)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final IUserService userService;
    private final KycVerificationServiceImpl kycVerificationService;

    @Operation(
            summary = "Buscar usuario por ID",
            description = "Obtiene los datos de un usuario según su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error al buscar el usuario.",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ExtendedBaseResponse<UserDto>> findUserById(@PathVariable UUID id) {
        ExtendedBaseResponse<UserDto> response = userService.findUserById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Obtener usuario logueado",
            description = "Devuelve los datos del usuario que ha iniciado sesión actualmente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario logueado encontrado exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró un usuario logueado.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno al obtener el usuario logueado.",
                    content = @Content
            )
    })
    @GetMapping()
    public ResponseEntity<ExtendedBaseResponse<UserDto>> getLoggedUser() {
        ExtendedBaseResponse<UserDto> response = userService.findOnlineUser();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Consulta todos los KYC registrados por el usuario",
            description = "Obtiene todos los procesos de verificación KYC de un usuario por su ID. Devuelve un listado vacío si no hay registros."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado cargado correctamente",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/{id}/kyc/all")
    public ResponseEntity<ExtendedBaseResponse<List<KycVerificationResponseDTO>>> getAllKycByUserId(
            @PathVariable("id") UUID userId) {

        ExtendedBaseResponse<List<KycVerificationResponseDTO>> response =
                kycVerificationService.getAllKcyByUserId(userId);

        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Consulta KYC por usuario y opcionalmente por estado",
            description = "Obtiene todos los procesos de verificación KYC de un usuario. Se puede filtrar por estado. Devuelve un listado vacío si no hay registros."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado cargado correctamente",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/{id}/kyc")
    public ResponseEntity<ExtendedBaseResponse<List<KycVerificationResponseDTO>>> getAllKycByUserIdAndStatus(
            @PathVariable("id") UUID userId,
            @RequestParam(value = "status", required = false) KycStatus status
    ) {
        return ResponseEntity.ok(
                kycVerificationService.getAllByUserIdAndOptionalStatus(userId, status)
        );
    }

    @Operation(
            summary = "Actualizar datos de un usuario",
            description = "Permite actualizar los datos de un usuario proporcionado su ID y los nuevos valores."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario actualizado exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserUpdateRequestDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error al actualizar los datos del usuario.",
                    content = @Content
            )
    })
    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ExtendedBaseResponse<UserUpdateResponseDto> updateUser(@Valid @RequestBody UserUpdateRequestDto updateUserDto) {
        return userService.updateUser(updateUserDto);
    }

    @Operation(summary = "Listar todos los usuarios",
            description = "Permite recuperar una lista de todos los usuarios.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Comentarios recuperados exitosamente.",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExtendedBaseResponse.class))
                    }),
            @ApiResponse(responseCode = "500", description = "Error del servidor.", content = {@Content})
    })
    @GetMapping("/list")
    public ExtendedBaseResponse<List<UserDto>> getAllUsers() {
        return userService.userLists();
    }

    @Operation(summary = "Listar todos los usuarios activos",
            description = "Permite recuperar una lista de todos los usuarios que estén activos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios activos recuperados exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExtendedBaseResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error del servidor.", content = @Content)
    })
    @GetMapping("/list-active")
    public ExtendedBaseResponse<List<UserDto>> getAllActiveUsers() {
        return userService.userListsActive();
    }

    @Operation(summary = "Cambiar el estado de un usuario ( true / false )",
            description = "Permite activar o desactivar un usuario proporcionándole el ID y el nuevo estado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado del usuario actualizado exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExtendedBaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error del servidor.", content = @Content)
    })
    @PutMapping("/change-status")
    public ExtendedBaseResponse<UserDto> changeUserStatus(@Valid @RequestBody UserStatusRequestDto data) {
        return userService.changeUserStatus(data);
    }

    @Operation(summary = "Eliminar un usuario por ID",
            description = "Permite eliminar un usuario dado su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExtendedBaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error del servidor.", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ExtendedBaseResponse<String> deleteUser(@PathVariable UUID id) {
        return userService.deleteUserById(id);
    }


    @Operation(
            summary = "Obtener empresas asociadas a un usuario",
            description = "Obtiene un listado de todas las empresas (entidades asociadas) de un usuario por su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de empresas cargado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = KycVerificationResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado o no tiene empresas asociadas",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/{id}/companies")
// 💡 CAMBIO PRINCIPAL: El ResponseEntity ahora usa List<CompanyResponseDTO>
    public ResponseEntity<ExtendedBaseResponse<List<CompanyResponseDTO>>> getCompaniesByUserId(
            @PathVariable("id") UUID userId) {

        // El userService devuelve el tipo de dato correcto: List<CompanyResponseDTO>
        ExtendedBaseResponse<List<CompanyResponseDTO>> response =
                userService.getCompaniesByUserId(userId);

        return ResponseEntity.ok(response);
    }

}
