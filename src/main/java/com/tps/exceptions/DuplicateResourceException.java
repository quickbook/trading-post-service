package com.tps.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a client tries to create a resource that already exists
 * (e.g., duplicate username or email).
 * Responds with HTTP 409 CONFLICT.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {
    
	private static final long serialVersionUID = 1L;

	public DuplicateResourceException(String message) {
        super(message);
    }
}

