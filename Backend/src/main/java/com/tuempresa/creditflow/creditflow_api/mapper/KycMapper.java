package com.tuempresa.creditflow.creditflow_api.mapper;

import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycVerificationResponseDTO;
import com.tuempresa.creditflow.creditflow_api.enums.KycEntityType;
import com.tuempresa.creditflow.creditflow_api.model.KycVerification;
import org.mapstruct.*;
import java.util.Optional;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface KycMapper {

    @Mapping(target = "idKyc", source = "idKyc")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "verificationNotes", source = "verificationNotes", qualifiedByName = "mapNotes")
    @Mapping(target = "externalReferenceId", source = "externalReferenceId")
    @Mapping(target = "submissionDate", source = "submissionDate")
    @Mapping(target = "verificationDate", source = "verificationDate")
    @Mapping(target = "kycEntityType", expression = "java(kyc.getEntityType())")
    @Mapping(target = "entityName", expression = "java(mapEntityName(kyc))")
    @Mapping(target = "document1Url", expression = "java(mapDocument1(kyc))")
    @Mapping(target = "document2Url", expression = "java(mapDocument2(kyc))")
    @Mapping(target = "document3Url", expression = "java(mapDocument3(kyc))")
    KycVerificationResponseDTO toResponseDto(KycVerification kyc);

    // ===================== Métodos auxiliares =====================

    @Named("mapUrl")
    default String mapUrl(String url) {
        return Optional.ofNullable(url).orElse("URL no disponible");
    }

    @Named("mapNotes")
    default String mapNotes(String notes) {
        return Optional.ofNullable(notes).orElse("Sin notas de verificación");
    }

    default String mapEntityName(KycVerification kyc) {
        if (kyc.getEntityType() == KycEntityType.USER && kyc.getUser() != null) {
            return kyc.getUser().getFirstName() + " " + kyc.getUser().getLastName();
        } else if (kyc.getEntityType() == KycEntityType.COMPANY && kyc.getCompany() != null) {
            return kyc.getCompany().getCompany_name();
        }
        return "Desconocido";
    }

    // Documentos obligatorios (los 3 deben existir)
    default String mapDocument1(KycVerification kyc) {
        validarDocumentos(kyc);
        return mapUrl(kyc.getDocument1Url());
    }

    default String mapDocument2(KycVerification kyc) {
        validarDocumentos(kyc);
        return mapUrl(kyc.getDocument2Url());
    }

    default String mapDocument3(KycVerification kyc) {
        validarDocumentos(kyc);
        return mapUrl(kyc.getDocument3Url());
    }

    private void validarDocumentos(KycVerification kyc) {
        if (kyc.getDocument1Url() == null || kyc.getDocument2Url() == null || kyc.getDocument3Url() == null) {
            throw new IllegalStateException("Los tres documentos son obligatorios para la verificación KYC.");
        }
    }
}
