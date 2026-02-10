package kz.aitu.music_library_api.exception;
/**
 * Exception thrown when attempting to create a resource that already exists.
 * Extends InvalidInputException to maintain exception hierarchy.
 */
public class DuplicateResourceException extends InvalidInputException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceType, String identifier) {
        super(String.format("%s with identifier '%s' already exists", resourceType, identifier));
    }
}