package kz.aitu.music_library_api.model;

import kz.aitu.music_library_api.exception.InvalidInputException;

import java.util.ArrayList;
import java.util.List;


public class Playlist implements Validatable<Playlist>, Printable {
    private int id;
    private String name;
    private List<Media> items;
    private String description;

    public Playlist(String name) {
        this.name = name;
        this.items = new ArrayList<>();
    }

    public Playlist(int id, String name) {
        this.id = id;
        this.name = name;
        this.items = new ArrayList<>();
    }

    public Playlist(String name, String description) {
        this.name = name;
        this.description = description;
        this.items = new ArrayList<>();
    }

    public Playlist(int id, String name, String description, List<Media> items) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.items = items != null ? items : new ArrayList<>();
    }

    public void addMedia(Media media) {
        if (media != null && !items.contains(media)) {
            items.add(media);
            System.out.println("Added: " + media.getName() + " to playlist '" + name + "'");
        }
    }

    public void removeMedia(Media media) {
        if (items.remove(media)) {
            System.out.println("Removed: " + media.getName() + " from playlist '" + name + "'");
        }
    }

    public int getTotalDuration() {
        return items.stream()
                .mapToInt(Media::getDuration)
                .sum();
    }

    public String getFormattedTotalDuration() {
        int totalSeconds = getTotalDuration();
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else {
            return String.format("%dm %ds", minutes, seconds);
        }
    }

    public void playAll() {
        System.out.println("\nPlaying playlist: " + name);
        System.out.println("─────────────────────────────────────────");
        if (items.isEmpty()) {
            System.out.println("Playlist is empty!");
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i).toString());
        }
        System.out.println("─────────────────────────────────────────");
        System.out.println("Total: " + items.size() + " items | Duration: " + getFormattedTotalDuration());
    }

    @Override
    public void validate() throws InvalidInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Playlist name cannot be empty");
        }
        if (name.length() > 100) {
            throw new InvalidInputException("Playlist name cannot exceed 100 characters");
        }
    }

    @Override
    public void print() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         PLAYLIST INFORMATION           ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("  Name        : " + name);
        System.out.println("  Description : " + (description != null ? description : "N/A"));
        System.out.println("  Tracks      : " + items.size());
        System.out.println("  Duration    : " + getFormattedTotalDuration());
        System.out.println("╚════════════════════════════════════════╝");

        if (!items.isEmpty()) {
            System.out.println("\n  Track List:");
            for (int i = 0; i < items.size(); i++) {
                Media media = items.get(i);
                System.out.printf("    %2d. %s - %s [%s]%n",
                        i + 1, media.getName(), media.getCreator(), media.getFormattedDuration());
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Media> getItems() {
        return new ArrayList<>(items); // Return copy for encapsulation
    }

    public void setItems(List<Media> items) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("Playlist '%s': %d items (%s)",
                name, items.size(), getFormattedTotalDuration());
    }
}