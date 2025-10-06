package com.tuempresa.creditflow.creditflow_api.service;

import com.tuempresa.creditflow.creditflow_api.dtos.ExtendedBaseResponse;
import com.tuempresa.creditflow.creditflow_api.dtos.user.*;

public interface AuthService {
    ExtendedBaseResponse<AuthResponseDto> login(LoginRequestDto request);

    ExtendedBaseResponse<AuthResponseDto> register(RegisterRequestDto request);

    ExtendedBaseResponse<String> generatePasswordResetToken(EmailDto email);

    void resetPassword(ResetPasswordRequestDto request);
}

