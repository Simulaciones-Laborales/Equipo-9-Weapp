package com.tuempresa.creditflow.creditflow_api.exception;

import com.tuempresa.creditflow.creditflow_api.dto.BaseResponse;
import com.tuempresa.creditflow.creditflow_api.dto.ExtendedBaseResponse;
import com.tuempresa.creditflow.creditflow_api.exception.cloudinaryExc.ImageUploadException;
import com.tuempresa.creditflow.creditflow_api.exception.kycExc.KycNotFoundException;
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

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    // -------------------------
    // Existing user-related handlers (kept as in your project)
    // -------------------------
    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<BaseResponse> handleEmailNotFound(EmailNotFoundException ex) {
        log.warn("EmailNotFoundException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.error(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<BaseResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        log.warn("InvalidCredentialsException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(BaseResponse.error(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    @ExceptionHandler(UserDisabledException.class)
    public ResponseEntity<BaseResponse> handleUserDisabled(UserDisabledException ex) {
        log.warn("UserDisabledException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.error(HttpStatus.FORBIDDEN, ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<BaseResponse> handleUserNotFound(UserNotFoundException ex) {
        log.warn("UserNotFoundException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.error(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<BaseResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex, HttpServletRequest req) {
        log.warn("EmailAlreadyExistsException at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(BaseResponse.error(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(ContactAlreadyExistsException.class)
    public ResponseEntity<BaseResponse> handleEmailAlreadyExists(ContactAlreadyExistsException ex, HttpServletRequest req) {
        log.warn("PhoneAlreadyExistsException at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(BaseResponse.error(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(DniAlreadyExistsException.class)
    public ResponseEntity<BaseResponse> handleEmailAlreadyExists(DniAlreadyExistsException ex, HttpServletRequest req) {
        log.warn("DniAlreadyExistsException at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(BaseResponse.error(HttpStatus.CONFLICT, ex.getMessage()));
    }
    // ----------------------------
    //Kyc Error
    @ExceptionHandler(KycNotFoundException.class)
    public ResponseEntity<BaseResponse> handleKycNotFound(KycNotFoundException ex) {
        log.warn("KycNotFoundException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.error(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    // ----------------------------
    //Cloudinary Error
    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<BaseResponse> handleImageUploadException(ImageUploadException ex) {
        logger.error("Error subiendo imagen", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error subiendo imagen: " + ex.getMessage()));
    }

    // -------------------------
    // ResourceNotFoundException -> return BaseResponse (404)
    // -------------------------
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        log.warn("ResourceNotFoundException at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.error(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    // -------------------------
    // ConflictException -> return BaseResponse (409)
    // -------------------------
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<BaseResponse> handleConflict(ConflictException ex, HttpServletRequest req) {
        log.warn("ConflictException at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(BaseResponse.error(HttpStatus.CONFLICT, ex.getMessage()));
    }

    // -------------------------
    // Validation errors -> return ExtendedBaseResponse with validation map (400)
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
    // Generic fallback -> return BaseResponse (500)
    // -------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception at {}: {}", req.getRequestURI(), ex.toString(), ex);
        // No expongamos stacktrace al cliente
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"));
    }
}

