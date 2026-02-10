package kz.aitu.music_library_api.exception;
/**
 * Base exception for invalid input or validation errors.
 * All validation-related exceptions extend this class.
 */
public class InvalidInputException extends Exception {

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}