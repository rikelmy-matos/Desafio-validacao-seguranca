package com.devsuperior.bds04.controllers.exceptions;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.devsuperior.bds04.services.exceptions.DatabaseException;
import com.devsuperior.bds04.services.exceptions.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ResourceExceptionHandler {
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request){
		HttpStatus status = HttpStatus.NOT_FOUND;
		StandardError err = new StandardError(Instant.now(), status.value(), "Resource not found", e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
	
	@ExceptionHandler(DatabaseException.class)
	public ResponseEntity<StandardError> Database(DatabaseException e, HttpServletRequest request){
		HttpStatus status = HttpStatus.BAD_REQUEST;
		StandardError err = new StandardError(Instant.now(), status.value(), "Database Exception", e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationError> Validation(MethodArgumentNotValidException e, HttpServletRequest request){
		HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
		ValidationError err = new ValidationError(Instant.now(), status.value(), "Validation Exception", e.getMessage(), request.getRequestURI());
		for (FieldError error : e.getBindingResult().getFieldErrors()) {
			err.addError(error.getField(), error.getDefaultMessage());
		}
		return ResponseEntity.status(status).body(err);
	}
}
