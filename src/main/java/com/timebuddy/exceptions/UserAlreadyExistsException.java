package com.timebuddy.exceptions;

/**
 * Exception thrown when attempting to register a user with an already existing username.
 */
public class UserAlreadyExistsException extends RuntimeException {

    /**
     * Constructor for UserAlreadyExistsException.
     *
     * @param message The message to be included in the exception.
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}

