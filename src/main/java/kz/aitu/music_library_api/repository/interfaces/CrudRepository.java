package kz.aitu.music_library_api.repository.interfaces;

import kz.aitu.music_library_api.exception.DatabaseOperationException;
import kz.aitu.music_library_api.exception.ResourceNotFoundException;
import java.util.List;

public interface CrudRepository<T> {

    T create(T entity) throws DatabaseOperationException;

    List<T> getAll() throws DatabaseOperationException;

    T getById(Integer id) throws ResourceNotFoundException, DatabaseOperationException;

    T update(Integer id, T entity) throws ResourceNotFoundException, DatabaseOperationException;

    boolean delete(Integer id) throws ResourceNotFoundException, DatabaseOperationException;

    boolean exists(Integer id) throws DatabaseOperationException;
}