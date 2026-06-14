package com.umutgldn.tracking_service.exception;

import com.umutgldn.common.exception.BaseExceptionHandler;
import com.umutgldn.common.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(InvalidSimulationStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidState(
            InvalidSimulationStateException ex,
            HttpServletRequest request
    ) {
        ErrorResponse body = ErrorResponse.of(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

}
