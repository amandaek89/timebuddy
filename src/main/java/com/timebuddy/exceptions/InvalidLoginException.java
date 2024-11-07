package com.timebuddy.exceptions;

/**
 * Exception thrown when login credentials are invalid (either the username does not exist or the password is incorrect).
 */
public class InvalidLoginException extends RuntimeException {

    /**
     * Constructor for InvalidLoginException.
     *
     * @param message The message to be included in the exception.
     */
    public InvalidLoginException(String message) {
        super(message);
    }
}
