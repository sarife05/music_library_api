package kz.aitu.music_library_api.service;

import kz.aitu.music_library_api.exception.*;
import kz.aitu.music_library_api.model.Media;
import kz.aitu.music_library_api.repository.interfaces.MediaRepository;
import kz.aitu.music_library_api.service.interfaces.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;

    @Autowired
    public MediaServiceImpl(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    @Override
    public Media createMedia(Media media) throws InvalidInputException, DuplicateResourceException, DatabaseOperationException {
        media.validate();

        if (mediaRepository.existsByNameAndTypeAndCreator(media.getName(), media.getType(), media.getCreator())) {
            throw new DuplicateResourceException("Media",
                    String.format("%s '%s' by %s", media.getType(), media.getName(), media.getCreator()));
        }

        if (media.getDuration() > 86400) {
            throw new InvalidInputException("Media duration cannot exceed 24 hours (86400 seconds)");
        }

        return mediaRepository.create(media);
    }

    @Override
    public List<Media> getAllMedia() throws DatabaseOperationException {
        return mediaRepository.getAll();
    }

    @Override
    public Media getMediaById(Integer id) throws ResourceNotFoundException, DatabaseOperationException, InvalidInputException {
        if (id == null || id <= 0) {
            throw new InvalidInputException("Invalid media ID: " + id);
        }
        return mediaRepository.getById(id);
    }

    @Override
    public Media updateMedia(Integer id, Media media) throws ResourceNotFoundException, InvalidInputException, DatabaseOperationException {
        media.validate();

        if (!mediaRepository.exists(id)) {
            throw new ResourceNotFoundException("Media", id);
        }

        if (media.getDuration() > 86400) {
            throw new InvalidInputException("Media duration cannot exceed 24 hours");
        }

        return mediaRepository.update(id, media);
    }

    @Override
    public void deleteMedia(Integer id) throws ResourceNotFoundException, DatabaseOperationException {
        if (!mediaRepository.exists(id)) {
            throw new ResourceNotFoundException("Media", id);
        }

        mediaRepository.delete(id);
    }

    @Override
    public List<Media> getMediaByType(Media.MediaType type) throws DatabaseOperationException {
        if (type == null) {
            throw new IllegalArgumentException("Media type cannot be null");
        }
        return mediaRepository.findByType(type);
    }

    @Override
    public List<Media> getMediaByCreator(String creator) throws DatabaseOperationException {
        if (creator == null || creator.trim().isEmpty()) {
            throw new IllegalArgumentException("Creator name cannot be empty");
        }
        return mediaRepository.findByCreator(creator);
    }

    @Override
    public List<Media> searchMediaByName(String keyword) throws DatabaseOperationException {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }
        return mediaRepository.searchByName(keyword);
    }
}