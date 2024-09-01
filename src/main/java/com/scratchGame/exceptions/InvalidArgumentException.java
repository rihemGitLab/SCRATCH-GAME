package com.scratchGame.exceptions;

/**
 * Custom exception to indicate invalid arguments passed to a method or constructor.
 */
public class InvalidArgumentException extends RuntimeException {

    /**
     * Constructs a new InvalidArgumentException with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidArgumentException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidArgumentException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public InvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
