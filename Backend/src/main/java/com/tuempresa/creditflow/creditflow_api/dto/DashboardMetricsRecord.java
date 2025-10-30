package com.tuempresa.creditflow.creditflow_api.dto;

import java.util.Map;

public record DashboardMetricsRecord(
        // Cantidad total de solicitudes
        Long totalCreditApplications,
        // Cantidad de solicitudes por estado (usa el enum EEstadoSolicitud)
        Map<String, Long> applicationsByStatus,
        // Cantidad de clientes (Usuarios)
        Long totalUsers,
        // Cantidad de empresas
        Long totalCompanies,
        // Cantidad total de KYC
        Long totalKyc,
        // Cantidad de KYC por estado (usa el enum KycStatus)
        Map<String, Long> kycByStatus
) {
}
