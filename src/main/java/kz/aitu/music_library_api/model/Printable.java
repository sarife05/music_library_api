package kz.aitu.music_library_api.model;

public interface Printable {

    void print();

    default void printWithBorder() {
        System.out.println("═══════════════════════════════════════════════════════");
        print();
        System.out.println("═══════════════════════════════════════════════════════");
    }
}