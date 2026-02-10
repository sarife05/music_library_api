package kz.aitu.music_library_api.model;

public interface Playable {

    int getDuration();

    void play();

    default void pause() {
        System.out.println("Paused");
    }

    default void stop() {
        System.out.println("Stopped");
    }

    static boolean isValidDuration(int duration) {
        return duration > 0 && duration < 86400; // Max 24 hours
    }

    static String formatDuration(int seconds) {
        if (seconds < 0) return "Invalid duration";

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%d:%02d", minutes, secs);
        }
    }
}