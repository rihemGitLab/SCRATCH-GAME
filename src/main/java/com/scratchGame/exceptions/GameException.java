package com.scratchGame.exceptions;

/**
 * A custom exception class for handling game-specific errors.
 */
public class GameException extends RuntimeException {


    public GameException(String message) {
        super(message);
    }

}
