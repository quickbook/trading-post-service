package com.tps.config;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.tps.dto.ErrorDetails;
import com.tps.dto.response.ApiResponse;
import com.tps.exceptions.DuplicateResourceException;
import com.tps.exceptions.InvalidCredentialsException;
import com.tps.exceptions.ResourceNotFoundException;
import com.tps.service.ExceptionLoggingService;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	private final ExceptionLoggingService exceptionLogger;

	public GlobalExceptionHandler(ExceptionLoggingService exceptionLogger) {
		this.exceptionLogger = exceptionLogger;
	}

	// ---- VALIDATION ----
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Object>> handleValidationErrors(MethodArgumentNotValidException ex,
			WebRequest request, HttpServletRequest httpReq) {

		Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
				.collect(Collectors.toMap(f -> f.getField(), f -> f.getDefaultMessage(), (a, b) -> a));

		ErrorDetails details = new ErrorDetails("VALIDATION_FAILED", "Input validation failed", fieldErrors,
				ex.getClass().getSimpleName());

		ApiResponse<Object> response = ApiResponse.<Object>builder().success(false).message("Validation failed")
				.data(null).errorDetails(details).status(HttpStatus.BAD_REQUEST)
				.path(request.getDescription(false).replace("uri=", "")).timestamp(System.currentTimeMillis()).build();

		// async persist
		exceptionLogger.save(httpReq, ex, HttpStatus.BAD_REQUEST.value(), extractClientId(httpReq));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	// ---- DUPLICATE ----
	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ApiResponse<Object>> handleDuplicateResource(DuplicateResourceException ex,
			WebRequest request, HttpServletRequest httpReq) {

		ErrorDetails details = new ErrorDetails("RESOURCE_CONFLICT", ex.getMessage(), null,
				ex.getClass().getSimpleName());

		ApiResponse<Object> response = ApiResponse.<Object>builder().success(false).message("Resource conflict")
				.data(null).errorDetails(details).status(HttpStatus.CONFLICT)
				.path(request.getDescription(false).replace("uri=", "")).timestamp(System.currentTimeMillis()).build();

		exceptionLogger.save(httpReq, ex, HttpStatus.CONFLICT.value(), extractClientId(httpReq));
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}

	// ---- NOT FOUND ----
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request,
			HttpServletRequest httpReq) {

		ErrorDetails details = new ErrorDetails("RESOURCE_NOT_FOUND", ex.getMessage(), null,
				ex.getClass().getSimpleName());

		ApiResponse<Object> response = ApiResponse.<Object>builder().success(false).message("Resource not found")
				.data(null).errorDetails(details).status(HttpStatus.BAD_REQUEST) // keep your original choice (400)
				.path(request.getDescription(false).replace("uri=", "")).timestamp(System.currentTimeMillis()).build();

		exceptionLogger.save(httpReq, ex, HttpStatus.BAD_REQUEST.value(), extractClientId(httpReq));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	// ---- INVALID CREDS ----
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ApiResponse<Object>> handleInvalidCredentials(InvalidCredentialsException ex,
			WebRequest request, HttpServletRequest httpReq) {

		ErrorDetails details = new ErrorDetails("INVALID_CREDENTIALS", ex.getMessage(), null,
				ex.getClass().getSimpleName());

		ApiResponse<Object> response = ApiResponse.<Object>builder().success(false).message("Authentication failed")
				.data(null).errorDetails(details).status(HttpStatus.UNAUTHORIZED) // 401
				.path(request.getDescription(false).replace("uri=", "")).timestamp(System.currentTimeMillis()).build();

		exceptionLogger.save(httpReq, ex, HttpStatus.UNAUTHORIZED.value(), extractClientId(httpReq));
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

	// ---- RUNTIME ----
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiResponse<Object>> handleRuntime(RuntimeException ex, WebRequest request,
			HttpServletRequest httpReq) {

		ErrorDetails details = new ErrorDetails("RUNTIME_ERROR", ex.getMessage(), null, ex.getClass().getSimpleName());

		ApiResponse<Object> response = ApiResponse.<Object>builder().success(false).message("Runtime error").data(null)
				.errorDetails(details).status(HttpStatus.INTERNAL_SERVER_ERROR)
				.path(request.getDescription(false).replace("uri=", "")).timestamp(System.currentTimeMillis()).build();

		exceptionLogger.save(httpReq, ex, HttpStatus.INTERNAL_SERVER_ERROR.value(), extractClientId(httpReq));
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	// ---- DATA INTEGRITY ----
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiResponse<Object>> handleDataIntegrity(DataIntegrityViolationException ex,
			WebRequest request, HttpServletRequest httpReq) {

		ErrorDetails details = new ErrorDetails("DB_CONSTRAINT_VIOLATION",
				"Database constraint violation: " + ex.getMostSpecificCause().getMessage(), null,
				ex.getClass().getSimpleName());

		ApiResponse<Object> response = ApiResponse.<Object>builder().success(false).message("Database error").data(null)
				.errorDetails(details).status(HttpStatus.INTERNAL_SERVER_ERROR)
				.path(request.getDescription(false).replace("uri=", "")).timestamp(System.currentTimeMillis()).build();

		exceptionLogger.save(httpReq, ex, HttpStatus.INTERNAL_SERVER_ERROR.value(), extractClientId(httpReq));
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	// ---- GENERIC ----
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex, WebRequest request,
			HttpServletRequest httpReq) {

		ErrorDetails details = new ErrorDetails("INTERNAL_ERROR", "Unexpected error occurred", null,
				ex.getClass().getSimpleName());

		ApiResponse<Object> response = ApiResponse.<Object>builder().success(false).message("Unexpected error")
				.data(null).errorDetails(details).status(HttpStatus.INTERNAL_SERVER_ERROR)
				.path(request.getDescription(false).replace("uri=", "")).timestamp(System.currentTimeMillis()).build();

		exceptionLogger.save(httpReq, ex, HttpStatus.INTERNAL_SERVER_ERROR.value(), extractClientId(httpReq));
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	// ---- Helpers ----
	private String extractClientId(HttpServletRequest req) {
		String xff = req.getHeader("X-Forwarded-For");
		if (xff != null && !xff.isBlank())
			return xff.split(",")[0].trim();
		return req.getRemoteAddr();

	}
}
