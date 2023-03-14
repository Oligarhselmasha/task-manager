package com.oligarhselmasha.taskmanager.controllers;

import com.oligarhselmasha.taskmanager.exceptions.MissingException;
import com.oligarhselmasha.taskmanager.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIValidationException(final MissingException missingException) {
        return new ErrorResponse(
                missingException.getMessage()
        );
    }
}
