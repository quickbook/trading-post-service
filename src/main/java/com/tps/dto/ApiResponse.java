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
 private boolean success;   
 private String message;    
 private T data;            
 private ErrorDetails errorDetails;
 private HttpStatus status;        
 private String path;             
 private long timestamp;  
 }
