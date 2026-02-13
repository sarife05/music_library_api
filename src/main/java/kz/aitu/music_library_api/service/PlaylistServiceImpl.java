package kz.aitu.music_library_api.service;

import kz.aitu.music_library_api.exception.*;
import kz.aitu.music_library_api.model.Playlist;
import kz.aitu.music_library_api.repository.interfaces.MediaRepository;
import kz.aitu.music_library_api.repository.interfaces.PlaylistRepository;
import kz.aitu.music_library_api.service.interfaces.CacheService;
import kz.aitu.music_library_api.service.interfaces.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Playlist Service Implementation with Caching
 * Implements caching for frequently accessed data (getAllPlaylists)
 * Automatically invalidates cache on create/update/delete operations
 */
@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final MediaRepository mediaRepository;
    private final CacheService cacheService;

    // Cache key constants
    private static final String CACHE_KEY_ALL_PLAYLISTS = "playlist:all";
    private static final String CACHE_KEY_PLAYLIST_BY_ID = "playlist:id:";
    private static final String CACHE_KEY_PLAYLIST_BY_NAME = "playlist:name:";

    @Autowired
    public PlaylistServiceImpl(PlaylistRepository playlistRepository, 
                               MediaRepository mediaRepository,
                               CacheService cacheService) {
        this.playlistRepository = playlistRepository;
        this.mediaRepository = mediaRepository;
        this.cacheService = cacheService;
    }

    @Override
    public Playlist createPlaylist(Playlist playlist) throws InvalidInputException, DuplicateResourceException, DatabaseOperationException {
        playlist.validate();

        if (playlistRepository.existsByName(playlist.getName())) {
            throw new DuplicateResourceException("Playlist", playlist.getName());
        }

        Playlist createdPlaylist = playlistRepository.create(playlist);
        
        // Invalidate all playlist-related caches after creation
        invalidatePlaylistCaches();
        
        return createdPlaylist;
    }

    @Override
    public List<Playlist> getAllPlaylists() throws DatabaseOperationException {
        // Try to get from cache first
        Optional<List<Playlist>> cachedPlaylists = cacheService.getCachedList(CACHE_KEY_ALL_PLAYLISTS);
        
        if (cachedPlaylists.isPresent()) {
            return cachedPlaylists.get();
        }
        
        // Cache miss - fetch from database
        List<Playlist> playlists = playlistRepository.getAll();
        
        // Store in cache for future requests
        cacheService.cacheList(CACHE_KEY_ALL_PLAYLISTS, playlists);
        
        return playlists;
    }

    @Override
    public Playlist getPlaylistById(Integer id) throws ResourceNotFoundException, DatabaseOperationException, InvalidInputException {
        if (id == null || id <= 0) {
            throw new InvalidInputException("Invalid playlist ID: " + id);
        }
        
        String cacheKey = CACHE_KEY_PLAYLIST_BY_ID + id;
        
        // Try to get from cache first
        Optional<Playlist> cachedPlaylist = cacheService.getCached(cacheKey, Playlist.class);
        
        if (cachedPlaylist.isPresent()) {
            return cachedPlaylist.get();
        }
        
        // Cache miss - fetch from database
        Playlist playlist = playlistRepository.getById(id);
        
        // Store in cache
        cacheService.cache(cacheKey, playlist);
        
        return playlist;
    }

    @Override
    public Playlist updatePlaylist(Integer id, Playlist playlist) throws ResourceNotFoundException, InvalidInputException, DatabaseOperationException {
        playlist.validate();

        if (!playlistRepository.exists(id)) {
            throw new ResourceNotFoundException("Playlist", id);
        }

        Playlist existing = playlistRepository.getById(id);
        if (!existing.getName().equalsIgnoreCase(playlist.getName())) {
            if (playlistRepository.existsByName(playlist.getName())) {
                throw new DuplicateResourceException("Playlist", playlist.getName());
            }
        }

        Playlist updatedPlaylist = playlistRepository.update(id, playlist);
        
        // Invalidate all playlist-related caches after update
        invalidatePlaylistCaches();
        
        return updatedPlaylist;
    }

    @Override
    public void deletePlaylist(Integer id) throws ResourceNotFoundException, DatabaseOperationException {
        if (!playlistRepository.exists(id)) {
            throw new ResourceNotFoundException("Playlist", id);
        }

        playlistRepository.delete(id);
        
        // Invalidate all playlist-related caches after deletion
        invalidatePlaylistCaches();
    }

    @Override
    public void addMediaToPlaylist(Integer playlistId, Integer mediaId) throws ResourceNotFoundException, DatabaseOperationException {
        if (!playlistRepository.exists(playlistId)) {
            throw new ResourceNotFoundException("Playlist", playlistId);
        }

        if (!mediaRepository.exists(mediaId)) {
            throw new ResourceNotFoundException("Media", mediaId);
        }

        playlistRepository.addMediaToPlaylist(playlistId, mediaId);
        
        // Invalidate playlist caches after adding media
        // This affects the specific playlist and the "all playlists" list
        invalidatePlaylistCaches();
    }

    @Override
    public void removeMediaFromPlaylist(Integer playlistId, Integer mediaId) throws DatabaseOperationException {
        playlistRepository.removeMediaFromPlaylist(playlistId, mediaId);
        
        // Invalidate playlist caches after removing media
        invalidatePlaylistCaches();
    }

    @Override
    public Playlist getPlaylistByName(String name) throws ResourceNotFoundException, DatabaseOperationException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Playlist name cannot be empty");
        }

        String cacheKey = CACHE_KEY_PLAYLIST_BY_NAME + name.toLowerCase();
        
        // Try to get from cache first
        Optional<Playlist> cachedPlaylist = cacheService.getCached(cacheKey, Playlist.class);
        
        if (cachedPlaylist.isPresent()) {
            return cachedPlaylist.get();
        }
        
        // Cache miss - fetch from database
        Playlist playlist = playlistRepository.findByName(name);
        if (playlist == null) {
            throw new ResourceNotFoundException("Playlist", name);
        }
        
        // Store in cache
        cacheService.cache(cacheKey, playlist);
        
        return playlist;
    }

    /**
     * Invalidate all playlist-related cache entries
     * Called after create, update, delete, or modification operations
     */
    private void invalidatePlaylistCaches() {
        cacheService.invalidatePattern("playlist:*");
    }
    
    /**
     * Manual cache clearing method (can be exposed via controller if needed)
     */
    public void clearPlaylistCache() {
        invalidatePlaylistCaches();
    }
}
