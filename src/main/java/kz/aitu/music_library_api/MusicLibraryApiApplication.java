package kz.aitu.music_library_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = "com.musiclibrary")
public class MusicLibraryApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicLibraryApiApplication.class, args);

        System.out.println("\n" + "=".repeat(80));
        System.out.println("Music Library API Started Successfully!");
        System.out.println("=".repeat(80));
        System.out.println("=".repeat(80) + "\n");
    }
}