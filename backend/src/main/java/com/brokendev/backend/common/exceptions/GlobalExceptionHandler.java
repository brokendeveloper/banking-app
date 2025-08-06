package com.brokendev.backend.common.exceptions;

import com.brokendev.backend.common.exceptions.dto.ErrorResponseDTO;
import com.brokendev.backend.common.exceptions.dto.ValidationErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponseDTO> buildError(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status).body(
                new ErrorResponseDTO(
                        status.value(),
                        error,
                        message,
                        LocalDateTime.now()
                )
        );
    }

    private ResponseEntity<ValidationErrorResponseDTO> buildValidationError(HttpStatus status, String error, String generalMessage, Map<String, String> fieldErrors) {
        return ResponseEntity.status(status).body(
                new ValidationErrorResponseDTO(
                        status.value(),
                        error,
                        generalMessage,
                        fieldErrors,
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(UsernameNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, "User Not Found", ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return buildError(HttpStatus.CONFLICT, "User Already Exists", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(IllegalArgumentException ex) {
        return buildError(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCardNotFound(CardNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, "Card Not Found", ex.getMessage());
    }

    @ExceptionHandler(CardBlockedException.class)
    public ResponseEntity<ErrorResponseDTO> handleCardBlocked(CardBlockedException ex) {
        return buildError(HttpStatus.FORBIDDEN, "Card Blocked", ex.getMessage());
    }

    @ExceptionHandler(UserAccountNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccountNotFound(UserAccountNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, "Account Not Found", ex.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponseDTO> handleInsufficientBalance(InsufficientBalanceException ex) {
        return buildError(HttpStatus.BAD_REQUEST, "Insufficient Balance", ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnauthorizedOperation(UnauthorizedOperationException ex) {
        return buildError(HttpStatus.FORBIDDEN, "Unauthorized Operation", ex.getMessage());
    }

    @ExceptionHandler(PixTransferNotAllowedException.class)
    public ResponseEntity<ErrorResponseDTO> handlePixTransferNotAllowed(PixTransferNotAllowedException ex) {
        return buildError(HttpStatus.FORBIDDEN, "PIX Transfer Not Allowed", ex.getMessage());
    }

    @ExceptionHandler(InvestmentNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvestmentNotFound(InvestmentNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, "Investment Not Found", ex.getMessage());
    }

    @ExceptionHandler(InvestmentOwnershipException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvestmentOwnership(InvestmentOwnershipException ex) {
        return buildError(HttpStatus.FORBIDDEN, "Investment Ownership Error", ex.getMessage());
    }

    @ExceptionHandler(InvestmentAlreadyRedeemedException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvestmentAlreadyRedeemed(InvestmentAlreadyRedeemedException ex) {
        return buildError(HttpStatus.BAD_REQUEST, "Investment Already Redeemed", ex.getMessage());
    }

    @ExceptionHandler(InvestmentNotMaturedException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvestmentNotMatured(InvestmentNotMaturedException ex) {
        return buildError(HttpStatus.BAD_REQUEST, "Investment Not Matured", ex.getMessage());
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotificationNotFound(NotificationNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, "Notification Not Found", ex.getMessage());
    }

    @ExceptionHandler(NotificationAccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotificationAccessDenied(NotificationAccessDeniedException ex) {
        return buildError(HttpStatus.FORBIDDEN, "Notification Access Denied", ex.getMessage());
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidPassword(InvalidPasswordException ex) {
        return buildError(HttpStatus.UNAUTHORIZED, "Invalid Password", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {

            if (error instanceof FieldError) {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            } else {

                errors.put(error.getObjectName(), error.getDefaultMessage());
            }
        });

        String generalMessage = "Invalid data in Request";

        return buildValidationError(
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                generalMessage,
                errors
        );
    }

    // Handler genérico para exceções não tratadas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(Exception ex) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
    }
}