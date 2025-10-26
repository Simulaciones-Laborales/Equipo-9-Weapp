package com.tuempresa.creditflow.creditflow_api.exception;

import com.tuempresa.creditflow.creditflow_api.dto.BaseResponse;
import com.tuempresa.creditflow.creditflow_api.dto.ExtendedBaseResponse;
import com.tuempresa.creditflow.creditflow_api.exception.cloudinaryExc.ImageUploadException;
import com.tuempresa.creditflow.creditflow_api.exception.cloudinaryExc.RiskDocumentNotFoundException;
import com.tuempresa.creditflow.creditflow_api.exception.kycExc.KycBadRequestException;
import com.tuempresa.creditflow.creditflow_api.exception.kycExc.CompanyNotVerifiedException;
import com.tuempresa.creditflow.creditflow_api.exception.kycExc.KycNotFoundException;
import com.tuempresa.creditflow.creditflow_api.exception.kycExc.UserNotVerifiedException;
import com.tuempresa.creditflow.creditflow_api.exception.userExc.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // -------------------------
    // 404 Not Found
    // -------------------------
    @ExceptionHandler({
            EmailNotFoundException.class,
            UserNotFoundException.class,
            KycNotFoundException.class,
            ResourceNotFoundException.class
    })
    public ResponseEntity<BaseResponse> handleNotFound(RuntimeException ex, HttpServletRequest req) {
        log.warn("NotFoundException at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.error(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    // -------------------------
    // 401 Unauthorized
    // -------------------------
    @ExceptionHandler({ InvalidCredentialsException.class })
    public ResponseEntity<BaseResponse> handleUnauthorized(RuntimeException ex, HttpServletRequest req) {
        log.warn("UnauthorizedException at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(BaseResponse.error(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    // -------------------------
    // 403 Forbidden
    // -------------------------
    @ExceptionHandler({
            UserDisabledException.class,
            UserNotVerifiedException.class,
            CompanyNotVerifiedException.class,
            UnauthorizedException.class
    })
    public ResponseEntity<BaseResponse> handleForbidden(RuntimeException ex, HttpServletRequest req) {
        log.warn("ForbiddenException at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.error(HttpStatus.FORBIDDEN, ex.getMessage()));
    }

    // -------------------------
    // 409 Conflict
    // -------------------------
    @ExceptionHandler({
            EmailAlreadyExistsException.class,
            ContactAlreadyExistsException.class,
            DniAlreadyExistsException.class,
            ConflictException.class
    })
    public ResponseEntity<BaseResponse> handleConflict(RuntimeException ex, HttpServletRequest req) {
        log.warn("ConflictException at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(BaseResponse.error(HttpStatus.CONFLICT, ex.getMessage()));
    }

    // -------------------------
    // 500 Internal Server Error: Cloudinary errors
    // -------------------------
    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<BaseResponse> handleImageUpload(ImageUploadException ex, HttpServletRequest req) {
        logger.error("ImageUploadException at {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Error subiendo imagen: " + ex.getMessage()));
    }

    @ExceptionHandler(RiskDocumentNotFoundException.class)
    public ResponseEntity<BaseResponse> handleRiskDocumentNotFound(RiskDocumentNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.error(HttpStatus.NOT_FOUND, ex.getMessage()));
    }
    // -------------------------
    // 400 Bad Request: Validation errors
    // -------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExtendedBaseResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        log.warn("Validation error at {}: {}", req.getRequestURI(), ex.getMessage());

        Map<String, String> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fe -> fe.getField(),
                        fe -> fe.getDefaultMessage(),
                        (existing, replacement) -> existing // keep first if duplicate
                ));

        BaseResponse base = BaseResponse.badRequest("Validation failed for one or more fields");
        ExtendedBaseResponse<Map<String, String>> payload = ExtendedBaseResponse.of(base, validationErrors);

        return ResponseEntity.badRequest().body(payload);
    }

    // -------------------------
    // 500 Internal Server Error: Generic fallback
    // -------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception at {}: {}", req.getRequestURI(), ex.toString(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"));
    }

    @ExceptionHandler(KycBadRequestException.class)
    public ResponseEntity<ExtendedBaseResponse<String>> handleBadRequestException(KycBadRequestException ex, HttpServletRequest request) {
        log.warn("[BAD REQUEST] {} at {}", ex.getMessage(), request.getRequestURI());

        return ResponseEntity
                .badRequest()
                .body(ExtendedBaseResponse.of(BaseResponse.badRequest(ex.getMessage()), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExtendedBaseResponse<String>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        assert ex.getRequiredType() != null;
        String message = String.format("Valor inválido '%s' para el parámetro '%s'. Esperado tipo: %s",
                ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());

        log.warn("[ARG MISMATCH] {} at {}", message, request.getRequestURI());

        return ResponseEntity
                .badRequest()
                .body(ExtendedBaseResponse.of(BaseResponse.badRequest(message), message));
    }

    // -------------------------
// IllegalArgumentException -> return BaseResponse (400)
// -------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        log.warn("IllegalArgumentException at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

}
