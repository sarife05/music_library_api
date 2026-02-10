package kz.aitu.music_library_api.exception;
/**
 * Exception thrown when a requested resource cannot be found.
 */
public class ResourceNotFoundException extends Exception {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceType, int id) {
        super(String.format("%s with ID %d not found", resourceType, id));
    }

    public ResourceNotFoundException(String resourceType, String identifier) {
        super(String.format("%s '%s' not found", resourceType, identifier));
    }
}