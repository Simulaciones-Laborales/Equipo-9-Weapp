package com.tuempresa.creditflow.creditflow_api.service;
import com.tuempresa.creditflow.creditflow_api.dto.ExtendedBaseResponse;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycFileUploadRequestDTO;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycStatusUpdateDTO;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycVerificationResponseDTO;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycVerifiedCompanyResponseDTO;
import com.tuempresa.creditflow.creditflow_api.enums.KycStatus;

import java.util.List;
import java.util.UUID;

/**
 * Interfaz para el servicio de gestión de verificaciones KYC (Know Your Customer)
 * de usuarios y empresas.
 */
public interface IKycVerificationService {
    /**
     * Inicia un proceso KYC subiendo archivos y creando el registro local.
     *
     * @param dto DTO con los documentos y la entidad a verificar
     * @return DTO con la información de la verificación creada
     */
    ExtendedBaseResponse<KycVerificationResponseDTO> startVerificationWithFiles(KycFileUploadRequestDTO dto);

    /**
     * Obtiene todas las verificaciones KYC registradas.
     *
     * @return Lista de verificaciones
     */
    ExtendedBaseResponse<List<KycVerificationResponseDTO>> getAll();
    ExtendedBaseResponse<List<KycVerifiedCompanyResponseDTO>> getVerifiedCompaniesDetails();

    /**
     * Obtiene todas las verificaciones de un usuario por su ID.
     *
     * @param userId ID del usuario
     * @return Lista de verificaciones del usuario
     */
    ExtendedBaseResponse<List<KycVerificationResponseDTO>> getAllKcyByUserId(UUID userId);

    /**
     * Obtiene todas las verificaciones de un usuario por su ID y un estado opcional.
     *
     * @param userId ID del usuario
     * @param status Estado a filtrar (opcional, puede ser null)
     * @return Lista filtrada de verificaciones
     */
    ExtendedBaseResponse<List<KycVerificationResponseDTO>> getAllByUserIdAndOptionalStatus(UUID userId, KycStatus status);

    /**
     * Obtiene una verificación KYC por su ID.
     *
     * @param id ID de la verificación
     * @return DTO con la información de la verificación
     */
    ExtendedBaseResponse<KycVerificationResponseDTO> getById(UUID id);

    /**
     * Actualiza el estado de una verificación KYC.
     *
     * @param id  ID de la verificación
     * @param dto DTO con el nuevo estado y notas
     * @return DTO actualizado de la verificación
     */
    ExtendedBaseResponse<KycVerificationResponseDTO> updateStatus(UUID id, KycStatusUpdateDTO dto);

    /**
     * Elimina una verificación KYC por su ID.
     *
     * @param id ID de la verificación
     */
    ExtendedBaseResponse<String> delete(UUID id);

    ExtendedBaseResponse<List<KycVerificationResponseDTO>> getFiltered(String kycEntityType, String status);

    ExtendedBaseResponse<KycVerificationResponseDTO> getCompanyStatusById(UUID companyId);
}
