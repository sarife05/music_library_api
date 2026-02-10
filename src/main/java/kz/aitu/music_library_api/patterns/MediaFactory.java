package kz.aitu.music_library_api.patterns;


import kz.aitu.music_library_api.model.Media;
import kz.aitu.music_library_api.model.Song;
import kz.aitu.music_library_api.model.Podcast;
import org.springframework.stereotype.Component;

@Component
public class MediaFactory {

    public Media createMedia(Media.MediaType type, String name, int duration, String creator) {
        if (type == null) {
            throw new IllegalArgumentException("Media type cannot be null");
        }

        return switch (type) {
            case SONG -> new Song(name, duration, creator);
            case PODCAST -> new Podcast(name, duration, creator);
        };
    }

    public Song createSong(String name, int duration, String creator,
                           String album, String genre, double price) {
        Song song = new Song(name, duration, creator, album, genre);
        song.setPrice(price);
        return song;
    }

    public Podcast createPodcast(String name, int duration, String creator,
                                 String host, int episodeNumber, String category) {
        return new Podcast(name, duration, creator, host, episodeNumber, category);
    }

    public Media createMediaFromData(int id, String name, int duration, String type,
                                     String creator, String album, String genre, double price,
                                     String host, int episodeNumber, String category) {
        Media.MediaType mediaType = Media.MediaType.valueOf(type);

        if (mediaType == Media.MediaType.SONG) {
            return new Song(id, name, duration, creator, album, genre, price);
        } else {
            return new Podcast(id, name, duration, creator, host, episodeNumber, category);
        }
    }

    public boolean isValidMediaType(String type) {
        try {
            Media.MediaType.valueOf(type.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}