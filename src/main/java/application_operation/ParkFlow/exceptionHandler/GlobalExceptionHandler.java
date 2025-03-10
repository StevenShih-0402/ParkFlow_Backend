package application_operation.ParkFlow.exceptionHandler;

import application_operation.ParkFlow.Response.ErrorResponse;
import application_operation.ParkFlow.exception.HandleException;
import application_operation.ParkFlow.exception.JwtTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HandleException.class)
    public ResponseEntity<ErrorResponse> handleException(HandleException ex) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ErrorResponse
                        .builder()
                        .errorCode("9000")
                        .message(ex.getMessage())
                        .data(null)
                        .build()
                );
    }

    @ExceptionHandler(JwtTokenException.class)
    public ResponseEntity<ErrorResponse> handleJwtTokenException(JwtTokenException ex) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ErrorResponse
                        .builder()
                        .errorCode("9000")
                        .message(ex.getMessage())
                        .data(null)
                        .build()
                );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ErrorResponse
                        .builder()
                        .errorCode("9000")
                        .message(ex.getMessage())
                        .data(null)
                        .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exception(Exception ex) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ErrorResponse
                        .builder()
                        .errorCode("9000")
                        .message(ex.getMessage())
                        .data(null)
                        .build()
                );
    }
}
