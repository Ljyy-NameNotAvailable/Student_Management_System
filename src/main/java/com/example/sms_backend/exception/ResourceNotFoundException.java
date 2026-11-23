package com.example.sms_backend.exception;

/**
 * No @ResponseStatus here on purpose: GlobalExceptionHandler maps this to 404
 * and owns the response body. Carrying both an annotation and an advice entry
 * works (the advice wins) but splits the status decision across two files.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException student(Long id) {
        return new ResourceNotFoundException("Student not found with id " + id);
    }
}
