package com.santander.meetup.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;


/**
 * Class to handle errors appropriately to be sent as responses.
 */
@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Retrieves a new API error response entity.
     *
     * @param code    the internal code.
     * @param status  the HTTP status.
     * @param message the error message.
     * @param errors  a list of detailed errors.
     * @return a new API error response entity.
     */
    public ResponseEntity<Object> getErrorResponse(int code, HttpStatus status, String message, List<String> errors) {
        ApiError apiError = new ApiError(code, status, message, errors);
        return new ResponseEntity<>(apiError, new HttpHeaders(), status);
    }

    /**
     * Retrieves a new API error response entity.
     *
     * @param code    the internal code.
     * @param status  the HTTP status.
     * @param message the error message.
     * @param error   the detailed errors.
     * @return a new API error response entity.
     */
    public ResponseEntity<Object> getErrorResponse(int code, HttpStatus status, String message, String error) {
        ApiError apiError = new ApiError(code, status, message, error.trim());
        return new ResponseEntity<>(apiError, new HttpHeaders(), status);
    }

    /**
     * Triggered when an unsupported HTTP method for a given end point was received.
     *
     * @param ex      the exception to handle.
     * @param headers {@code HttpHeaders}.
     * @param status  {@code HttpStatus}.
     * @param request {@code WebRequest}.
     * @return a {@code ResponseEntity} object with the error handled.
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        int code = ApiErrorCode.REQUEST_METHOD_NOT_SUPPORTED;
        HttpStatus httpStatus = HttpStatus.METHOD_NOT_ALLOWED;
        String message = "Unsupported HTTP method";
        StringBuilder error = new StringBuilder();

        error.append(ex.getMethod());
        error.append(" method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(method -> error.append(method + " "));

        return getErrorResponse(code, httpStatus, message, error.toString());
    }

    /**
     * Triggered when an unsupported media type for a given end point was received.
     *
     * @param ex      the exception to handle.
     * @param headers {@code HttpHeaders}.
     * @param status  {@code HttpStatus}.
     * @param request {@code WebRequest}.
     * @return a {@code ResponseEntity} object with the error handled.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        int code = ApiErrorCode.MEDIA_TYPE_NOT_SUPPORTED;
        HttpStatus httpStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        String message = "Unsupported media type";
        StringBuilder error = new StringBuilder();

        error.append(ex.getContentType());
        error.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(mediaType -> error.append(mediaType + ", "));

        return getErrorResponse(code, httpStatus, message, error.toString());
    }

    /**
     * Triggered when an argument annotated with {@code @Valid} failed on being validated.
     *
     * @param ex      the exception to handle.
     * @param headers {@code HttpHeaders}.
     * @param status  {@code HttpStatus}.
     * @param request {@code WebRequest}.
     * @return a {@code ResponseEntity} object with the error handled.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        int code = ApiErrorCode.METHOD_ARGUMENT_NOT_VALID;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        StringBuilder message = new StringBuilder("Invalid arguments: ");
        List<String> errors = new ArrayList<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
            message.append(error.getField() + " ");
        }

        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
            message.append(error.getObjectName() + " ");
        }

        return getErrorResponse(code, httpStatus, message.toString(), errors);
    }

    /**
     * Triggered when a {@code required} request parameter was missing.
     *
     * @param headers {@code HttpHeaders}.
     * @param status  {@code HttpStatus}.
     * @param request {@code WebRequest}.
     * @return a {@code ResponseEntity} object with the error handled.
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        int code = ApiErrorCode.MISSING_SERVLET_REQUEST_PARAMETER;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String message = "Parameter missing";
        String error = ex.getParameterName() + " parameter is missing";
        return getErrorResponse(code, httpStatus, message, error);
    }

    /**
     * Triggered when a fatal problem occurred with content mapping.
     *
     * @param ex the exception to handle.
     * @return a {@code ResponseEntity} object with the error handled.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        int code = ApiErrorCode.MESSAGE_NOT_READABLE;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String message = "Invalid body";
        String error = ex.getCause().getMessage().split("\\s()\\s")[0];
        return getErrorResponse(code, httpStatus, message, error);
    }

    /**
     * Triggered when a requested DML operation resulted in a violation of a defined integrity constraint.
     *
     * @param ex the exception to handle.
     * @return a {@code ResponseEntity} object with the error handled.
     */
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        int code = ApiErrorCode.CONSTRAINT_VIOLATION;
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        StringBuilder message = new StringBuilder("Invalid arguments: ");
        List<String> errors = new ArrayList<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
            message.append(violation.getPropertyPath() + " ");
        }

        return getErrorResponse(code, httpStatus, message.toString(), errors);
    }

    /**
     * Triggered when a method argument was not the expected type.
     *
     * @param ex the exception to handle.
     * @return a {@code ResponseEntity} object with the error handled.
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        int code = ApiErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String message = "Invalid " + ex.getName() + "argument type";
        String error = ex.getName() + " should be of type " + ex.getRequiredType().getName();
        return getErrorResponse(code, httpStatus, message, error);
    }

    /**
     * Triggered when a given authentication request was rejected because the credentials are invalid.
     *
     * @param ex the exception to handle.
     * @return a {@code ResponseEntity} object with the error handled.
     */
    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<Object> handleBadCredentials(BadCredentialsException ex) {
        int code = ApiErrorCode.BAD_CREDENTIALS;
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        String message = "Bad credentials";
        String error = ex.getMessage();
        return getErrorResponse(code, httpStatus, message, error);
    }

    /**
     * Triggered when a given entity was not found.
     *
     * @param ex the exception to handle.
     * @return a {@code ResponseEntity} object with the error handled.
     */
    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
        int code = ApiErrorCode.ENTITY_NOT_FOUND;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String message = ex.getEntityName() + " was not found";
        String error = ex.getEntityName() + " was not found for parameter " + ex.getId();
        return getErrorResponse(code, httpStatus, message, error);
    }

    /**
     * Triggered when a given entity already exists in the database.
     *
     * @param ex the exception to handle.
     * @return a {@code ResponseEntity} object with the error handled.
     */
    @ExceptionHandler({DuplicateEntityException.class})
    public ResponseEntity<Object> handleDuplicateResource(DuplicateEntityException ex) {
        int code = ApiErrorCode.DUPLICATE_ENTITY;
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        String message = ex.getEntityName() + " already exists";
        StringBuilder error = new StringBuilder();

        error.append("{" + ex.getValues().get(0).toString());
        ex.getValues().stream().skip(1).forEach(value -> error.append(", " + value));
        error.append("} already exists. Select another {");
        error.append(ex.getUniqueFields().get(0).toString());
        ex.getUniqueFields().stream().skip(1).forEach(field -> error.append(", " + field));
        error.append("}");

        return getErrorResponse(code, httpStatus, message, error.toString());
    }

    /**
     * Triggered when assigning a value to an attribute is not allowed.
     *
     * @param ex the exception to handle.
     * @return a {@code ResponseEntity} object with the error handled.
     */
    @ExceptionHandler({ValueNotAllowedException.class})
    public ResponseEntity<Object> handleDuplicateResource(ValueNotAllowedException ex) {
        int code = ApiErrorCode.VALUE_NOT_ALLOWED;
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        String message = "Value not allowed";
        String error = "The " + ex.getAttribute() + " {" + ex.getValue() + "} is not allowed because " + ex.getReason();
        return getErrorResponse(code, httpStatus, message, error);
    }

    /**
     * Default Handler. It deals with all other exceptions that don't have specific handlers.
     *
     * @param ex the exception to handle.
     * @return a {@code ResponseEntity} object with the error handled.
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(Exception ex) {
        int code = ApiErrorCode.INTERNAL;
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "It's not you. It's us. We are having some problems";
        String error = "error occurred";
        return getErrorResponse(code, httpStatus, message, error);
    }
}
