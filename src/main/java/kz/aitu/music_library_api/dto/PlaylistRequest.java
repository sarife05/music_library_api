package kz.aitu.music_library_api.dto;

import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistRequest {

    @NotBlank(message = "Playlist name is required")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private List<Integer> mediaIds;

    public PlaylistRequest() {
        this.mediaIds = new ArrayList<>();
    }

    public PlaylistRequest(String name) {
        this.name = name;
        this.mediaIds = new ArrayList<>();
    }

    public PlaylistRequest(String name, String description) {
        this.name = name;
        this.description = description;
        this.mediaIds = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Integer> getMediaIds() {
        return mediaIds;
    }

    public void setMediaIds(List<Integer> mediaIds) {
        this.mediaIds = mediaIds != null ? mediaIds : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "PlaylistRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", mediaIds=" + (mediaIds != null ? mediaIds.size() : 0) +
                '}';
    }
}