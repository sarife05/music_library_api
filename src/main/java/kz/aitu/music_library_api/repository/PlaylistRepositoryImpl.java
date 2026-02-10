package kz.aitu.music_library_api.repository;

import kz.aitu.music_library_api.exception.DatabaseOperationException;
import kz.aitu.music_library_api.exception.ResourceNotFoundException;
import kz.aitu.music_library_api.model.Media;
import kz.aitu.music_library_api.model.Playlist;
import kz.aitu.music_library_api.repository.interfaces.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PlaylistRepositoryImpl implements PlaylistRepository {

    private final JdbcTemplate jdbcTemplate;
    private final MediaRepositoryImpl mediaRepository;

    @Autowired
    public PlaylistRepositoryImpl(JdbcTemplate jdbcTemplate, MediaRepositoryImpl mediaRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.mediaRepository = mediaRepository;
    }

    private final RowMapper<Playlist> playlistRowMapper = (rs, rowNum) -> {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String description = rs.getString("description");

        List<Media> items = null;
        try {
            items = getPlaylistMedia(id);
        } catch (DatabaseOperationException e) {
            throw new RuntimeException(e);
        }

        return new Playlist(id, name, description, items);
    };

    @Override
    public Playlist create(Playlist entity) throws DatabaseOperationException {
        String sql = "INSERT INTO playlists (name, description) VALUES (?, ?)";

        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, entity.getName());
                ps.setString(2, entity.getDescription());
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key != null) {
                entity.setId(key.intValue());
            }

            for (Media media : entity.getItems()) {
                if (media.getId() != 0 && media.getId() > 0) {
                    addMediaToPlaylist(entity.getId(), media.getId());
                }
            }

            return entity;

        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to create playlist", e);
        }
    }

    @Override
    public List<Playlist> getAll() throws DatabaseOperationException {
        String sql = "SELECT * FROM playlists ORDER BY id";
        try {
            return jdbcTemplate.query(sql, playlistRowMapper);
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to retrieve all playlists", e);
        }
    }

    @Override
    public Playlist getById(Integer id) throws ResourceNotFoundException, DatabaseOperationException {
        String sql = "SELECT * FROM playlists WHERE id = ?";
        try {
            List<Playlist> results = jdbcTemplate.query(sql, playlistRowMapper, id);
            if (results.isEmpty()) {
                throw new ResourceNotFoundException("Playlist", id);
            }
            return results.get(0);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to retrieve playlist by ID", e);
        }
    }

    @Override
    public Playlist update(Integer id, Playlist entity) throws ResourceNotFoundException, DatabaseOperationException {
        if (!exists(id)) {
            throw new ResourceNotFoundException("Playlist", id);
        }

        String sql = "UPDATE playlists SET name = ?, description = ? WHERE id = ?";

        try {
            jdbcTemplate.update(sql, entity.getName(), entity.getDescription(), id);
            entity.setId(id);
            return entity;
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to update playlist", e);
        }
    }

    @Override
    public boolean delete(Integer id) throws ResourceNotFoundException, DatabaseOperationException {
        if (!exists(id)) {
            throw new ResourceNotFoundException("Playlist", id);
        }

        String sql = "DELETE FROM playlists WHERE id = ?";
        try {
            int rows = jdbcTemplate.update(sql, id);
            return rows > 0;
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to delete playlist", e);
        }
    }

    @Override
    public boolean exists(Integer id) throws DatabaseOperationException {
        String sql = "SELECT COUNT(*) FROM playlists WHERE id = ?";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
            return count != null && count > 0;
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to check playlist existence", e);
        }
    }

    @Override
    public void addMediaToPlaylist(Integer playlistId, Integer mediaId) throws DatabaseOperationException {
        String sql = "INSERT OR IGNORE INTO playlist_items (playlist_id, media_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sql, playlistId, mediaId);
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to add media to playlist", e);
        }
    }

    @Override
    public void removeMediaFromPlaylist(Integer playlistId, Integer mediaId) throws DatabaseOperationException {
        String sql = "DELETE FROM playlist_items WHERE playlist_id = ? AND media_id = ?";
        try {
            jdbcTemplate.update(sql, playlistId, mediaId);
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to remove media from playlist", e);
        }
    }

    @Override
    public List<Media> getPlaylistMedia(Integer playlistId) throws DatabaseOperationException {
        String sql = """
            SELECT m.id FROM media m
            INNER JOIN playlist_items pi ON m.id = pi.media_id
            WHERE pi.playlist_id = ?
            ORDER BY pi.position, m.id
        """;

        try {
            List<Integer> mediaIds = jdbcTemplate.queryForList(sql, Integer.class, playlistId);
            List<Media> mediaList = new ArrayList<>();

            for (Integer mediaId : mediaIds) {
                try {
                    Media media = mediaRepository.getById(mediaId);
                    mediaList.add(media);
                } catch (ResourceNotFoundException e) {
                    // Skip if media not found
                }
            }

            return mediaList;
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to get playlist media", e);
        }
    }

    @Override
    public boolean existsByName(String name) throws DatabaseOperationException {
        String sql = "SELECT COUNT(*) FROM playlists WHERE LOWER(name) = LOWER(?)";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name);
            return count != null && count > 0;
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to check playlist existence by name", e);
        }
    }

    @Override
    public Playlist findByName(String name) throws DatabaseOperationException {
        String sql = "SELECT * FROM playlists WHERE LOWER(name) = LOWER(?)";
        try {
            List<Playlist> results = jdbcTemplate.query(sql, playlistRowMapper, name);
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to find playlist by name", e);
        }
    }
}