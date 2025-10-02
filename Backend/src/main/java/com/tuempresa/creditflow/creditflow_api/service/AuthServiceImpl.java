package com.tuempresa.creditflow.creditflow_api.service;

public interface AuthService {
    ExtendedBaseResponse<AuthResponseDto> login(LoginRequestDto request);

    ExtendedBaseResponse<AuthResponseDto> register(RegisterRequestDto request);

    ExtendedBaseResponse<String> generatePasswordResetToken(EmailDto email);

    void resetPassword(ResetPasswordRequest request);
}

