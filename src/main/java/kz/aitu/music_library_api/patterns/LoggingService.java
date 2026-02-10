package kz.aitu.music_library_api.patterns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class LoggingService {

    private static LoggingService instance;
    private static final Logger logger = LoggerFactory.getLogger(LoggingService.class);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private LoggingService() {}

    public static LoggingService getInstance() {
        if (instance == null) {
            synchronized (LoggingService.class) {
                if (instance == null) {
                    instance = new LoggingService();
                }
            }
        }
        return instance;
    }

    public void logInfo(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        logger.info("[{}] {}", timestamp, message);
    }

    public void logError(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        logger.error("[{}] {}", timestamp, message);
    }

    public void logError(String message, Throwable throwable) {
        String timestamp = LocalDateTime.now().format(formatter);
        logger.error("[{}] {}", timestamp, message, throwable);
    }

    public void logDebug(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        logger.debug("[{}] {}", timestamp, message);
    }

    public void logWarn(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        logger.warn("[{}] {}", timestamp, message);
    }

    public void logApiRequest(String method, String endpoint) {
        logInfo(String.format("API Request: %s %s", method, endpoint));
    }

    public void logApiResponse(String method, String endpoint, int statusCode) {
        logInfo(String.format("API Response: %s %s - Status: %d", method, endpoint, statusCode));
    }
}