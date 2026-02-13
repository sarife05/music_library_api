package kz.aitu.music_library_api.controller;

import kz.aitu.music_library_api.exception.*;
import kz.aitu.music_library_api.model.*;
import kz.aitu.music_library_api.service.MediaServiceImpl;
import kz.aitu.music_library_api.service.PlaylistServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MusicLibraryController {

    private final MediaServiceImpl mediaService;
    private final PlaylistServiceImpl playlistService;

    @Autowired
    public MusicLibraryController(MediaServiceImpl mediaService,
                                  PlaylistServiceImpl playlistService) {
        this.mediaService = mediaService;
        this.playlistService = playlistService;
    }

    @PostMapping("/songs")
    public Media createSong(@RequestParam String name,
                            @RequestParam int duration,
                            @RequestParam String creator,
                            @RequestParam String album,
                            @RequestParam String genre,
                            @RequestParam double price)
            throws InvalidInputException, DatabaseOperationException {

        Song song = new Song(name, duration, creator, album, genre);
        song.setPrice(price);
        return mediaService.createMedia(song);
    }

    @PostMapping("/podcasts")
    public Media createPodcast(@RequestParam String name,
                               @RequestParam int duration,
                               @RequestParam String creator,
                               @RequestParam String host,
                               @RequestParam int episodeNumber,
                               @RequestParam String category)
            throws InvalidInputException, DatabaseOperationException {

        Podcast podcast = new Podcast(name, duration, creator, host, episodeNumber, category);
        return mediaService.createMedia(podcast);
    }

    @GetMapping("/media")
    public List<Media> getAllMedia() throws DatabaseOperationException {
        return mediaService.getAllMedia();
    }

    @GetMapping("/media/{id}")
    public Media getMediaById(@PathVariable int id)
            throws ResourceNotFoundException, InvalidInputException, DatabaseOperationException {
        return mediaService.getMediaById(id);
    }

    @PutMapping("/media/{id}")
    public Media updateMedia(@PathVariable int id,
                             @RequestBody Media media)
            throws ResourceNotFoundException, InvalidInputException, DatabaseOperationException {
        return mediaService.updateMedia(id, media);
    }

    @DeleteMapping("/media/{id}")
    public void deleteMedia(@PathVariable int id)
            throws ResourceNotFoundException, DatabaseOperationException {
        mediaService.deleteMedia(id);
    }

    @GetMapping("/media/type/{type}")
    public List<Media> getMediaByType(@PathVariable Media.MediaType type)
            throws DatabaseOperationException {
        return mediaService.getMediaByType(type);
    }

    @GetMapping("/media/search")
    public List<Media> searchMedia(@RequestParam String keyword)
            throws DatabaseOperationException {
        return mediaService.searchMediaByName(keyword);
    }

    @PostMapping("/playlists")
    public Playlist createPlaylist(@RequestBody Playlist playlist)
            throws InvalidInputException, DatabaseOperationException {
        return playlistService.createPlaylist(playlist);
    }

    @GetMapping("/playlists")
    public List<Playlist> getAllPlaylists() throws DatabaseOperationException {
        return playlistService.getAllPlaylists();
    }

    @GetMapping("/playlists/{id}")
    public Playlist getPlaylistById(@PathVariable int id)
            throws ResourceNotFoundException, InvalidInputException, DatabaseOperationException {
        return playlistService.getPlaylistById(id);
    }

    @DeleteMapping("/playlists/{id}")
    public void deletePlaylist(@PathVariable int id)
            throws ResourceNotFoundException, DatabaseOperationException {
        playlistService.deletePlaylist(id);
    }

    @PostMapping("/playlists/{playlistId}/media/{mediaId}")
    public void addMediaToPlaylist(@PathVariable int playlistId,
                                   @PathVariable int mediaId)
            throws ResourceNotFoundException, DatabaseOperationException {
        playlistService.addMediaToPlaylist(playlistId, mediaId);
    }

    @DeleteMapping("/playlists/{playlistId}/media/{mediaId}")
    public void removeMediaFromPlaylist(@PathVariable int playlistId,
                                        @PathVariable int mediaId)
            throws DatabaseOperationException {
        playlistService.removeMediaFromPlaylist(playlistId, mediaId);
    }
}