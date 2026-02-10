package kz.aitu.music_library_api.service.interfaces;

import kz.aitu.music_library_api.exception.*;
import kz.aitu.music_library_api.model.Media;
import java.util.List;

public interface MediaService {

    Media createMedia(Media media) throws InvalidInputException, DuplicateResourceException, DatabaseOperationException;

    List<Media> getAllMedia() throws DatabaseOperationException;

    Media getMediaById(Integer id) throws ResourceNotFoundException, DatabaseOperationException, InvalidInputException;

    Media updateMedia(Integer id, Media media) throws ResourceNotFoundException, InvalidInputException, DatabaseOperationException;

    void deleteMedia(Integer id) throws ResourceNotFoundException, DatabaseOperationException;

    List<Media> getMediaByType(Media.MediaType type) throws DatabaseOperationException;

    List<Media> getMediaByCreator(String creator) throws DatabaseOperationException;

    List<Media> searchMediaByName(String keyword) throws DatabaseOperationException;
}