package com.tuempresa.creditflow.creditflow_api.service.impl;

import com.tuempresa.creditflow.creditflow_api.dto.DashboardMetricsRecord;
import com.tuempresa.creditflow.creditflow_api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService implements IDashboardService {

    private final IUserService userService;
    private final CompanyService companyService; // Se asume su existencia para la entidad Empresa
    private final CreditApplicationService creditApplicationService;
    private final IKycVerificationService kycVerificationService;

    // Este método consolida todas las métricas en un único Record.
    @Override
    public DashboardMetricsRecord getSystemMetrics() {
        return new DashboardMetricsRecord(
                creditApplicationService.countTotalApplications(),
                creditApplicationService.countApplicationsByStatus(),
                userService.countTotalUsers(),
                companyService.countTotalCompanies(),
                kycVerificationService.countTotalKyc(),
                kycVerificationService.countKycByStatus()
        );
    }
}
