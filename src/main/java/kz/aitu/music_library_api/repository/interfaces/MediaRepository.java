package kz.aitu.music_library_api.repository.interfaces;

import kz.aitu.music_library_api.exception.DatabaseOperationException;
import kz.aitu.music_library_api.model.Media;

import java.util.List;

public interface MediaRepository extends CrudRepository<Media> {

    List<Media> findByType(Media.MediaType type) throws DatabaseOperationException;

    List<Media> findByCreator(String creator) throws DatabaseOperationException;

    List<Media> searchByName(String keyword) throws DatabaseOperationException;

    boolean existsByNameAndTypeAndCreator(String name, Media.MediaType type, String creator)
            throws DatabaseOperationException;
}