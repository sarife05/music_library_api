package kz.aitu.music_library_api.service.interfaces;

import kz.aitu.music_library_api.exception.*;
import kz.aitu.music_library_api.model.Playlist;
import java.util.List;

public interface PlaylistService {

    Playlist createPlaylist(Playlist playlist) throws InvalidInputException, DuplicateResourceException, DatabaseOperationException;

    List<Playlist> getAllPlaylists() throws DatabaseOperationException;

    Playlist getPlaylistById(Integer id) throws ResourceNotFoundException, DatabaseOperationException, InvalidInputException;

    Playlist updatePlaylist(Integer id, Playlist playlist) throws ResourceNotFoundException, InvalidInputException, DatabaseOperationException;

    void deletePlaylist(Integer id) throws ResourceNotFoundException, DatabaseOperationException;

    void addMediaToPlaylist(Integer playlistId, Integer mediaId) throws ResourceNotFoundException, DatabaseOperationException;

    void removeMediaFromPlaylist(Integer playlistId, Integer mediaId) throws DatabaseOperationException;

    Playlist getPlaylistByName(String name) throws ResourceNotFoundException, DatabaseOperationException;
}