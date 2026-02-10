package kz.aitu.music_library_api.model;

import kz.aitu.music_library_api.exception.InvalidInputException;

public class Song extends Media implements PricedItem {
    private String album;
    private String genre;
    private double price;

    public Song(String name, int duration, String creator) {
        super(name, duration, creator, MediaType.SONG);
        this.price = 0.99;
    }

    public Song(String name, int duration, String creator, String album, String genre) {
        super(name, duration, creator, MediaType.SONG);
        this.album = album;
        this.genre = genre;
        this.price = 0.99;
    }

    public Song(int id, String name, int duration, String creator, String album, String genre, double price) {
        super(id, name, duration, creator, MediaType.SONG);
        this.album = album;
        this.genre = genre;
        this.price = price;
    }

    @Override
    public String getDescription() {
        return String.format("Song: '%s' by %s from album '%s' (Genre: %s)",
                getName(), getCreator(), album != null ? album : "Unknown",
                genre != null ? genre : "Unknown");
    }

    @Override
    public void displayInfo() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("SONG DETAILS");
        System.out.println("═══════════════════════════════════════");
        System.out.println("Title    : " + getName());
        System.out.println("Artist   : " + getCreator());
        System.out.println("Album    : " + (album != null ? album : "N/A"));
        System.out.println("Genre    : " + (genre != null ? genre : "N/A"));
        System.out.println("Duration : " + getFormattedDuration());
        System.out.println("Price    : $" + String.format("%.2f", price));
        System.out.println("═══════════════════════════════════════");
    }

    @Override
    public void validate() throws InvalidInputException {
        super.validate();
        if (price < 0) {
            throw new InvalidInputException("Song price cannot be negative");
        }
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public double calculateTotalPrice(int quantity) {
        return price * quantity;
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
}