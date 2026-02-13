package kz.aitu.music_library_api.repository;

import kz.aitu.music_library_api.exception.DatabaseOperationException;
import kz.aitu.music_library_api.exception.ResourceNotFoundException;
import kz.aitu.music_library_api.model.*;
import kz.aitu.music_library_api.patterns.MediaFactory;
import kz.aitu.music_library_api.repository.interfaces.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class MediaRepositoryImpl implements MediaRepository {

    private final JdbcTemplate jdbcTemplate;
    private final MediaFactory mediaFactory;

    @Autowired
    public MediaRepositoryImpl(JdbcTemplate jdbcTemplate, MediaFactory mediaFactory) {
        this.jdbcTemplate = jdbcTemplate;
        this.mediaFactory = mediaFactory;
    }

    private RowMapper<Media> mediaRowMapper() {
        return (rs, rowNum) -> mediaFactory.createMediaFromData(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("duration"),
                rs.getString("type"),
                rs.getString("creator"),
                rs.getString("album"),
                rs.getString("genre"),
                rs.getDouble("price"),
                rs.getString("host"),
                rs.getInt("episode_number"),
                rs.getString("category")
        );
    }

    @Override
    public Media create(Media entity) throws DatabaseOperationException {
        String sql = """
            INSERT INTO media (name, duration, type, creator, album, genre, price, host, episode_number, category)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, entity.getName());
                ps.setInt(2, entity.getDuration());
                ps.setString(3, entity.getType().name());
                ps.setString(4, entity.getCreator());

                if (entity instanceof Song song) {
                    ps.setString(5, song.getAlbum());
                    ps.setString(6, song.getGenre());
                    ps.setDouble(7, song.getPrice());
                    ps.setString(8, null);
                    ps.setInt(9, 0);
                    ps.setString(10, null);
                } else if (entity instanceof Podcast podcast) {
                    ps.setString(5, null);
                    ps.setString(6, null);
                    ps.setDouble(7, 0.0);
                    ps.setString(8, podcast.getHost());
                    ps.setInt(9, podcast.getEpisodeNumber());
                    ps.setString(10, podcast.getCategory());
                }

                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key != null) {
                entity.setId(key.intValue());
            }

            return entity;

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseOperationException("Failed to create media: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Media> getAll() throws DatabaseOperationException {
        String sql = "SELECT * FROM media ORDER BY id";
        try {
            return jdbcTemplate.query(sql, mediaRowMapper());
        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseOperationException("Failed to retrieve all media: " + e.getMessage(), e);
        }
    }

    @Override
    public Media getById(Integer id) throws ResourceNotFoundException, DatabaseOperationException {
        String sql = "SELECT * FROM media WHERE id = ?";
        try {
            List<Media> results = jdbcTemplate.query(sql, mediaRowMapper(), id);
            if (results.isEmpty()) {
                throw new ResourceNotFoundException("Media", id);
            }
            return results.get(0);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseOperationException("Failed to retrieve media by id: " + e.getMessage(), e);
        }
    }

    @Override
    public Media update(Integer id, Media entity) throws ResourceNotFoundException, DatabaseOperationException {
        if (!exists(id)) {
            throw new ResourceNotFoundException("Media", id);
        }

        String sql = """
            UPDATE media 
            SET name = ?, duration = ?, creator = ?, album = ?, genre = ?, 
                price = ?, host = ?, episode_number = ?, category = ?
            WHERE id = ?
        """;

        try {
            jdbcTemplate.update(sql,
                    entity.getName(),
                    entity.getDuration(),
                    entity.getCreator(),
                    entity instanceof Song song ? song.getAlbum() : null,
                    entity instanceof Song song ? song.getGenre() : null,
                    entity instanceof Song song ? song.getPrice() : 0.0,
                    entity instanceof Podcast podcast ? podcast.getHost() : null,
                    entity instanceof Podcast podcast ? podcast.getEpisodeNumber() : 0,
                    entity instanceof Podcast podcast ? podcast.getCategory() : null,
                    id
            );

            entity.setId(id);
            return entity;

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseOperationException("Failed to update media: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer id) throws ResourceNotFoundException, DatabaseOperationException {
        if (!exists(id)) {
            throw new ResourceNotFoundException("Media", id);
        }

        String sql = "DELETE FROM media WHERE id = ?";
        try {
            int rows = jdbcTemplate.update(sql, id);
            return rows > 0;
        } catch (Exception e) {
    e.printStackTrace();
    throw new DatabaseOperationException("Failed to delete media: " + e.getMessage(), e);
}
    }

    @Override
    public boolean exists(Integer id) throws DatabaseOperationException {
        String sql = "SELECT COUNT(*) FROM media WHERE id = ?";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseOperationException("Failed to check media existence: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Media> findByType(Media.MediaType type) throws DatabaseOperationException {
        String sql = "SELECT * FROM media WHERE type = ? ORDER BY name";
        try {
            return jdbcTemplate.query(sql, mediaRowMapper(), type.name());
        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseOperationException("Failed to find media by type: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Media> findByCreator(String creator) throws DatabaseOperationException {
        String sql = "SELECT * FROM media WHERE LOWER(creator) = LOWER(?) ORDER BY name";
        try {
            return jdbcTemplate.query(sql, mediaRowMapper(), creator);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseOperationException("Failed to find media by creator: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Media> searchByName(String keyword) throws DatabaseOperationException {
        String sql = "SELECT * FROM media WHERE LOWER(name) LIKE LOWER(?) ORDER BY name";
        try {
            return jdbcTemplate.query(sql, mediaRowMapper(), "%" + keyword + "%");
        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseOperationException("Failed to find media by name: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByNameAndTypeAndCreator(String name, Media.MediaType type, String creator)
            throws DatabaseOperationException {
        String sql = "SELECT COUNT(*) FROM media WHERE LOWER(name) = LOWER(?) AND type = ? AND LOWER(creator) = LOWER(?)";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name, type.name(), creator);
            return count != null && count > 0;
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to check media existence", e);
        }
    }
}