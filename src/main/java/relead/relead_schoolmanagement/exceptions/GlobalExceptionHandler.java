package relead.relead_schoolmanagement.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> buildResponse(String message, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(AppExceptions.ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(AppExceptions.ResourceNotFoundException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND); // 404
    }

    @ExceptionHandler(AppExceptions.ResourceConflictException.class)
    public ResponseEntity<Map<String, Object>> handleResourceConflict(AppExceptions.ResourceConflictException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.CONFLICT); // 409
    }

    @ExceptionHandler(AppExceptions.BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(AppExceptions.BadRequestException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST); // 400
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        StringBuilder sb = new StringBuilder();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> sb.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; "));
        return buildResponse(sb.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, Object>> handleNullPointer(NullPointerException ex) {
        return buildResponse("A null value was encountered: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        return buildResponse("Internal server error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AppExceptions.CsvImportException.class)
    public ResponseEntity<Map<String, Object>> handleCsvImportException(AppExceptions.CsvImportException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
