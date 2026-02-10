package kz.aitu.music_library_api.patterns;

import kz.aitu.music_library_api.model.Media;
import kz.aitu.music_library_api.model.Playlist;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PlaylistBuilder {

    private Integer id;
    private String name;
    private String description;
    private List<Media> items;
    private String owner;
    private boolean isPublic;
    private String category;

    public PlaylistBuilder() {
        this.items = new ArrayList<>();
        this.isPublic = true; // default value
    }

    public static PlaylistBuilder builder() {
        return new PlaylistBuilder();
    }

    public PlaylistBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public PlaylistBuilder name(String name) {
        this.name = name;
        return this;
    }

    public PlaylistBuilder description(String description) {
        this.description = description;
        return this;
    }

    public PlaylistBuilder owner(String owner) {
        this.owner = owner;
        return this;
    }

    public PlaylistBuilder isPublic(boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }

    public PlaylistBuilder category(String category) {
        this.category = category;
        return this;
    }

    public PlaylistBuilder addMedia(Media media) {
        if (media != null) {
            this.items.add(media);
        }
        return this;
    }

    public PlaylistBuilder addAllMedia(List<Media> mediaList) {
        if (mediaList != null) {
            this.items.addAll(mediaList);
        }
        return this;
    }

    public PlaylistBuilder items(List<Media> items) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        return this;
    }

    public Playlist build() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalStateException("Playlist name is required");
        }

        Playlist playlist;
        if (id != null) {
            playlist = new Playlist(id, name, description, items);
        } else {
            playlist = new Playlist(name, description);
            playlist.setItems(items);
        }

        return playlist;
    }

    public PlaylistBuilder reset() {
        this.id = null;
        this.name = null;
        this.description = null;
        this.items = new ArrayList<>();
        this.owner = null;
        this.isPublic = true;
        this.category = null;
        return this;
    }
}