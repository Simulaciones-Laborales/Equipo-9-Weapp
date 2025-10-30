package com.tuempresa.creditflow.creditflow_api.controller;

import com.tuempresa.creditflow.creditflow_api.dto.BaseResponse;
import com.tuempresa.creditflow.creditflow_api.dto.DashboardMetricsRecord;
import com.tuempresa.creditflow.creditflow_api.dto.ExtendedBaseResponse;
import com.tuempresa.creditflow.creditflow_api.service.IDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dashboard & Estadísticas", description = "Métricas consolidadas para el Dashboard del sistema. (RESTRINGIDO)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

    private final IDashboardService dashboardService;

    @Operation(
            summary = "Obtener todas las métricas del sistema",
            description = "Devuelve un objeto único con todas las métricas agregadas (totales y por estado) para Solicitudes, Usuarios, Empresas y KYC."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Métricas cargadas exitosamente.",
            // Se usa el DashboardMetricsRecord como schema de respuesta
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DashboardMetricsRecord.class))
    )
    @GetMapping()
    public ResponseEntity<ExtendedBaseResponse<DashboardMetricsRecord>> getAllSystemMetrics() {

        DashboardMetricsRecord metrics = dashboardService.getSystemMetrics();

        BaseResponse baseResponse = BaseResponse.ok("Métricas consolidadas cargadas exitosamente.");

        ExtendedBaseResponse<DashboardMetricsRecord> response = new ExtendedBaseResponse<>(
                baseResponse.isError(),
                baseResponse.getStatusCode(),
                baseResponse.getStatusName(),
                baseResponse.message(),
                metrics
        );

        return ResponseEntity.ok(response);
    }
}
