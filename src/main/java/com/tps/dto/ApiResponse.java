package com.tps.dto;

 
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
 private boolean success;   // true or false
 private String message;    // optional description ("Fetched successfully", "Error", etc.)
 private T data;            // your actual payload (generic)
 private ErrorDetails errorDetails;
 private HttpStatus status;        // optional: HTTP status for easy debugging
 private String path;              // optional: endpoint path
 private long timestamp;  
 }
