package kz.aitu.music_library_api.dto;

import kz.aitu.music_library_api.model.Media;
import jakarta.validation.constraints.*;

public class MediaRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 200, message = "Name must be between 1 and 200 characters")
    private String name;

    @Positive(message = "Duration must be positive")
    @Max(value = 86400, message = "Duration cannot exceed 24 hours (86400 seconds)")
    private int duration;

    @NotBlank(message = "Creator is required")
    @Size(min = 1, max = 100, message = "Creator name must be between 1 and 100 characters")
    private String creator;

    @NotNull(message = "Media type is required")
    private Media.MediaType type;

    @Size(max = 200, message = "Album name cannot exceed 200 characters")
    private String album;

    @Size(max = 50, message = "Genre cannot exceed 50 characters")
    private String genre;

    @PositiveOrZero(message = "Price cannot be negative")
    @Max(value = 999, message = "Price cannot exceed 999")
    private Double price;

    @Size(max = 100, message = "Host name cannot exceed 100 characters")
    private String host;

    @PositiveOrZero(message = "Episode number cannot be negative")
    @Max(value = 10000, message = "Episode number cannot exceed 10000")
    private Integer episodeNumber;

    @Size(max = 50, message = "Category cannot exceed 50 characters")
    private String category;

    public MediaRequest() {}

    public MediaRequest(String name, int duration, String creator, Media.MediaType type) {
        this.name = name;
        this.duration = duration;
        this.creator = creator;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Media.MediaType getType() {
        return type;
    }

    public void setType(Media.MediaType type) {
        this.type = type;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(Integer episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "MediaRequest{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", creator='" + creator + '\'' +
                ", type=" + type +
                '}';
    }
}