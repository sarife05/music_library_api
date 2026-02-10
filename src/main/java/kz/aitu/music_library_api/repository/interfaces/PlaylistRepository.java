package kz.aitu.music_library_api.repository.interfaces;

import kz.aitu.music_library_api.exception.DatabaseOperationException;
import kz.aitu.music_library_api.model.Media;
import kz.aitu.music_library_api.model.Playlist;

import java.util.List;

public interface PlaylistRepository extends CrudRepository<Playlist> {

    void addMediaToPlaylist(Integer playlistId, Integer mediaId) throws DatabaseOperationException;

    void removeMediaFromPlaylist(Integer playlistId, Integer mediaId) throws DatabaseOperationException;

    List<Media> getPlaylistMedia(Integer playlistId) throws DatabaseOperationException;

    boolean existsByName(String name) throws DatabaseOperationException;

    Playlist findByName(String name) throws DatabaseOperationException;
}