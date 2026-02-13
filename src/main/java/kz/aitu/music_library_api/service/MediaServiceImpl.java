package kz.aitu.music_library_api.service;

import kz.aitu.music_library_api.exception.*;
import kz.aitu.music_library_api.model.Media;
import kz.aitu.music_library_api.repository.interfaces.MediaRepository;
import kz.aitu.music_library_api.service.interfaces.CacheService;
import kz.aitu.music_library_api.service.interfaces.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Media Service Implementation with Caching
 * Implements caching for frequently accessed data (getAllMedia)
 * Automatically invalidates cache on create/update/delete operations
 */
@Service
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;
    private final CacheService cacheService;

    // Cache key constants
    private static final String CACHE_KEY_ALL_MEDIA = "media:all";
    private static final String CACHE_KEY_MEDIA_BY_ID = "media:id:";
    private static final String CACHE_KEY_MEDIA_BY_TYPE = "media:type:";
    private static final String CACHE_KEY_MEDIA_BY_CREATOR = "media:creator:";
    private static final String CACHE_KEY_SEARCH = "media:search:";

    @Autowired
    public MediaServiceImpl(MediaRepository mediaRepository, CacheService cacheService) {
        this.mediaRepository = mediaRepository;
        this.cacheService = cacheService;
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

        Media createdMedia = mediaRepository.create(media);
        
        // Invalidate all media-related caches after creation
        invalidateMediaCaches();
        
        return createdMedia;
    }

    @Override
    public List<Media> getAllMedia() throws DatabaseOperationException {
        // Try to get from cache first
        Optional<List<Media>> cachedMedia = cacheService.getCachedList(CACHE_KEY_ALL_MEDIA);
        
        if (cachedMedia.isPresent()) {
            return cachedMedia.get();
        }
        
        // Cache miss - fetch from database
        List<Media> mediaList = mediaRepository.getAll();
        
        // Store in cache for future requests
        cacheService.cacheList(CACHE_KEY_ALL_MEDIA, mediaList);
        
        return mediaList;
    }

    @Override
    public Media getMediaById(Integer id) throws ResourceNotFoundException, DatabaseOperationException, InvalidInputException {
        if (id == null || id <= 0) {
            throw new InvalidInputException("Invalid media ID: " + id);
        }
        
        String cacheKey = CACHE_KEY_MEDIA_BY_ID + id;
        
        // Try to get from cache first
        Optional<Media> cachedMedia = cacheService.getCached(cacheKey, Media.class);
        
        if (cachedMedia.isPresent()) {
            return cachedMedia.get();
        }
        
        // Cache miss - fetch from database
        Media media = mediaRepository.getById(id);
        
        // Store in cache
        cacheService.cache(cacheKey, media);
        
        return media;
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

        Media updatedMedia = mediaRepository.update(id, media);
        
        // Invalidate all media-related caches after update
        invalidateMediaCaches();
        
        return updatedMedia;
    }

    @Override
    public void deleteMedia(Integer id) throws ResourceNotFoundException, DatabaseOperationException {
        if (!mediaRepository.exists(id)) {
            throw new ResourceNotFoundException("Media", id);
        }

        mediaRepository.delete(id);
        
        // Invalidate all media-related caches after deletion
        invalidateMediaCaches();
    }

    @Override
    public List<Media> getMediaByType(Media.MediaType type) throws DatabaseOperationException {
        if (type == null) {
            throw new IllegalArgumentException("Media type cannot be null");
        }
        
        String cacheKey = CACHE_KEY_MEDIA_BY_TYPE + type.name();
        
        // Try to get from cache first
        Optional<List<Media>> cachedMedia = cacheService.getCachedList(cacheKey);
        
        if (cachedMedia.isPresent()) {
            return cachedMedia.get();
        }
        
        // Cache miss - fetch from database
        List<Media> mediaList = mediaRepository.findByType(type);
        
        // Store in cache
        cacheService.cacheList(cacheKey, mediaList);
        
        return mediaList;
    }

    @Override
    public List<Media> getMediaByCreator(String creator) throws DatabaseOperationException {
        if (creator == null || creator.trim().isEmpty()) {
            throw new IllegalArgumentException("Creator name cannot be empty");
        }
        
        String cacheKey = CACHE_KEY_MEDIA_BY_CREATOR + creator.toLowerCase();
        
        // Try to get from cache first
        Optional<List<Media>> cachedMedia = cacheService.getCachedList(cacheKey);
        
        if (cachedMedia.isPresent()) {
            return cachedMedia.get();
        }
        
        // Cache miss - fetch from database
        List<Media> mediaList = mediaRepository.findByCreator(creator);
        
        // Store in cache
        cacheService.cacheList(cacheKey, mediaList);
        
        return mediaList;
    }

    @Override
    public List<Media> searchMediaByName(String keyword) throws DatabaseOperationException {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }
        
        String cacheKey = CACHE_KEY_SEARCH + keyword.toLowerCase();
        
        // Try to get from cache first
        Optional<List<Media>> cachedMedia = cacheService.getCachedList(cacheKey);
        
        if (cachedMedia.isPresent()) {
            return cachedMedia.get();
        }
        
        // Cache miss - fetch from database
        List<Media> mediaList = mediaRepository.searchByName(keyword);
        
        // Store in cache
        cacheService.cacheList(cacheKey, mediaList);
        
        return mediaList;
    }

    /**
     * Invalidate all media-related cache entries
     * Called after create, update, or delete operations
     */
    private void invalidateMediaCaches() {
        cacheService.invalidatePattern("media:*");
    }
    
    /**
     * Manual cache clearing method (can be exposed via controller if needed)
     */
    public void clearMediaCache() {
        invalidateMediaCaches();
    }
}
