package br.com.devdojo.handler;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.com.devdojo.error.ErrorDetails;
import br.com.devdojo.error.ResourceNotFoundDetails;
import br.com.devdojo.error.ResourceNotFoundException;
import br.com.devdojo.error.ValidationErrorDetails;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException rfnexception){
            ResourceNotFoundDetails rfnDetails = ResourceNotFoundDetails.Builder
            .newBuilder()
            .timestamp(new Date().getTime())
            .status(HttpStatus.NOT_FOUND.value())
            .title("Resource not found")
            .details(rfnexception.getMessage())
            .developerMessage(rfnexception.getClass().getName())
            .build();
            return new ResponseEntity<>(rfnDetails, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
    MethodArgumentNotValidException manvException, 
    HttpHeaders headers, HttpStatus status, WebRequest request){
        
        List<FieldError> fieldErrors = manvException.getBindingResult().getFieldErrors();
        String field = fieldErrors.stream().map(FieldError::getField).collect(Collectors.joining(","));
        String fieldMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(","));
        
        ValidationErrorDetails rfnDetails = ValidationErrorDetails.Builder
        .newBuilder()
        .timestamp(new Date().getTime())
        .status(HttpStatus.BAD_REQUEST.value())
        .title("Field Validation Error")
        .details("Field Validation Error")
        .developerMessage( manvException.getClass().getName())
        .field(field)
        .fieldMessage(fieldMessage)
        .build();
        return new ResponseEntity<>(rfnDetails, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
	Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

        ErrorDetails errorDetails = ErrorDetails.Builder
        .newBuilder()
        .timestamp(new Date().getTime())
        .status(status.value())
        .title("Internal Exception")
        .details(ex.getMessage())
        .developerMessage(ex.getClass().getName())
        .build();        
        return new ResponseEntity<>(errorDetails, headers, status);
	}
}