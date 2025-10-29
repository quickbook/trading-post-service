package com.tps.dto;

 

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {
 private String errorCode;           // e.g. VALIDATION_FAILED, ENTITY_NOT_FOUND, SERVER_ERROR
 private String errorMessage;        // human-readable error message
 private Map<String, String> fieldErrors; // validation field -> message (optional)
 private String exception;           // optional: exception class (e.g., ConstraintViolationException)
}

