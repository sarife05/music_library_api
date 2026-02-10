package kz.aitu.music_library_api.service;

import kz.aitu.music_library_api.exception.*;
import kz.aitu.music_library_api.model.Playlist;
import kz.aitu.music_library_api.repository.interfaces.MediaRepository;
import kz.aitu.music_library_api.repository.interfaces.PlaylistRepository;
import kz.aitu.music_library_api.service.interfaces.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final MediaRepository mediaRepository;

    @Autowired
    public PlaylistServiceImpl(PlaylistRepository playlistRepository, MediaRepository mediaRepository) {
        this.playlistRepository = playlistRepository;
        this.mediaRepository = mediaRepository;
    }

    @Override
    public Playlist createPlaylist(Playlist playlist) throws InvalidInputException, DuplicateResourceException, DatabaseOperationException {
        playlist.validate();

        if (playlistRepository.existsByName(playlist.getName())) {
            throw new DuplicateResourceException("Playlist", playlist.getName());
        }

        return playlistRepository.create(playlist);
    }

    @Override
    public List<Playlist> getAllPlaylists() throws DatabaseOperationException {
        return playlistRepository.getAll();
    }

    @Override
    public Playlist getPlaylistById(Integer id) throws ResourceNotFoundException, DatabaseOperationException, InvalidInputException {
        if (id == null || id <= 0) {
            throw new InvalidInputException("Invalid playlist ID: " + id);
        }
        return playlistRepository.getById(id);
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

        return playlistRepository.update(id, playlist);
    }

    @Override
    public void deletePlaylist(Integer id) throws ResourceNotFoundException, DatabaseOperationException {
        if (!playlistRepository.exists(id)) {
            throw new ResourceNotFoundException("Playlist", id);
        }

        playlistRepository.delete(id);
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
    }

    @Override
    public void removeMediaFromPlaylist(Integer playlistId, Integer mediaId) throws DatabaseOperationException {
        playlistRepository.removeMediaFromPlaylist(playlistId, mediaId);
    }

    @Override
    public Playlist getPlaylistByName(String name) throws ResourceNotFoundException, DatabaseOperationException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Playlist name cannot be empty");
        }

        Playlist playlist = playlistRepository.findByName(name);
        if (playlist == null) {
            throw new ResourceNotFoundException("Playlist", name);
        }

        return playlist;
    }
}