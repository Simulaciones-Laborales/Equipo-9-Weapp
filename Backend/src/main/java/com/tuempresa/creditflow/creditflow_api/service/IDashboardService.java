package com.tuempresa.creditflow.creditflow_api.service;

import com.tuempresa.creditflow.creditflow_api.dto.DashboardMetricsRecord;

public interface IDashboardService {
    DashboardMetricsRecord getSystemMetrics();
}
