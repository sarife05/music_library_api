package kz.aitu.music_library_api.exception;

/**
 * Exception thrown when a database operation fails.
 * Wraps SQLException and provides context about the operation.
 */
public class DatabaseOperationException extends Exception {

    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }

}