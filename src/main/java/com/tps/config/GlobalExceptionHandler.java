package com.tps.config;
 
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.tps.dto.ApiResponse;
import com.tps.dto.ErrorDetails;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Object>> handleValidationErrors(MethodArgumentNotValidException ex,
			WebRequest request) {

		Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
				.collect(Collectors.toMap(f -> f.getField(), f -> f.getDefaultMessage(), (a, b) -> a));

		ErrorDetails details = new ErrorDetails("VALIDATION_FAILED", "Input validation failed", fieldErrors,
				ex.getClass().getSimpleName());

		ApiResponse<Object> response = ApiResponse.<Object>builder().success(false).message("Validation failed")
				.data(null).errorDetails(details).status(HttpStatus.BAD_REQUEST)
				.path(request.getDescription(false).replace("uri=", "")).timestamp(System.currentTimeMillis()).build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiResponse<Object>> handleRuntime(RuntimeException ex, WebRequest request) {
		ErrorDetails details = new ErrorDetails("RUNTIME_ERROR", ex.getMessage(), null, ex.getClass().getSimpleName());

		ApiResponse<Object> response = ApiResponse.<Object>builder().success(false).message("Runtime error").data(null)
				.errorDetails(details).status(HttpStatus.INTERNAL_SERVER_ERROR)
				.path(request.getDescription(false).replace("uri=", "")).timestamp(System.currentTimeMillis()).build();

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex, WebRequest request) {
		ErrorDetails details = new ErrorDetails("INTERNAL_ERROR", "Unexpected error occurred", null,
				ex.getClass().getSimpleName());

		ApiResponse<Object> response = ApiResponse.<Object>builder().success(false).message("Unexpected error")
				.data(null).errorDetails(details).status(HttpStatus.INTERNAL_SERVER_ERROR)
				.path(request.getDescription(false).replace("uri=", "")).timestamp(System.currentTimeMillis()).build();

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}
}
