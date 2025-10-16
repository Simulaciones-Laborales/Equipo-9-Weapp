package com.tuempresa.creditflow.creditflow_api.mapper;

import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycVerificationResponseDTO;
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
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userFullName", expression = "java(kyc.getUser().getFirstName() + \" \" + kyc.getUser().getLastName())")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "selfieUrl", source = "selfieUrl", qualifiedByName = "mapUrl")
    @Mapping(target = "dniFrontUrl", source = "dniFrontUrl", qualifiedByName = "mapUrl")
    @Mapping(target = "dniBackUrl", source = "dniBackUrl", qualifiedByName = "mapUrl")
    KycVerificationResponseDTO toResponseDto(KycVerification kyc);

    @Named("mapUrl")
    default String mapUrl(String url) {
        return Optional.ofNullable(url).orElse("URL no disponible");
    }

    @Named("mapNotes")
    default String mapNotes(String notes) {
        return Optional.ofNullable(notes).orElse("Sin notas de verificación");
    }
}
