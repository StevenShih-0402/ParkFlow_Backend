package application_operation.ParkFlow.exceptionHandler;

import application_operation.ParkFlow.Response.ErrorResponse;
import application_operation.ParkFlow.exception.HandleException;
import application_operation.ParkFlow.exception.JwtTokenException;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HandleException.class)
    public ResponseEntity<ErrorResponse<Object>> handleException(HandleException ex) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ErrorResponse
                        .builder()
                        .message("Error")
                        .data(ex.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(Exception.class)  // 捕獲所有未處理的異常
    public ResponseEntity<ErrorResponse<Object>> handleJwtTokenException(JwtTokenException ex) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ErrorResponse
                        .builder()
                        .message("Error")
                        .data(ex.getMessage())
                        .build()
                );
    }
}
