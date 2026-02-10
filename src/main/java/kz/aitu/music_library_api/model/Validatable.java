package kz.aitu.music_library_api.model;

import kz.aitu.music_library_api.exception.InvalidInputException;

public interface Validatable<T> {

    void validate() throws InvalidInputException;

    default boolean isValid() {
        try {
            validate();
            return true;
        } catch (InvalidInputException e) {
            return false;
        }
    }

    @SafeVarargs
    static <T extends Validatable<T>> boolean validateAll(T... entities) throws InvalidInputException {
        for (T entity : entities) {
            entity.validate();
        }
        return true;
    }
}