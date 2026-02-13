package kz.aitu.music_library_api.patterns;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton Cache Manager - Thread-safe in-memory cache implementation
 * Follows SOLID principles:
 * - Single Responsibility: Manages cache operations only
 * - Open/Closed: Extensible through generic type support
 * - Liskov Substitution: Can be replaced with any cache implementation
 * - Interface Segregation: Clear, focused methods
 * - Dependency Inversion: Uses generic types, not concrete implementations
 */
@Component
public class CacheManager {

    private static volatile CacheManager instance;
    private static volatile boolean initialized = false;
    private final Map<String, CacheEntry<?>> cache;
    private final LoggingService loggingService;

    /**
     * Private constructor to enforce Singleton pattern
     */
    private CacheManager() {
        this.cache = new ConcurrentHashMap<>();
        this.loggingService = LoggingService.getInstance();
        
        // Only log initialization once
        if (!initialized) {
            synchronized (CacheManager.class) {
                if (!initialized) {
                    loggingService.logInfo("CacheManager initialized (Singleton instance created)");
                    initialized = true;
                }
            }
        }
    }

    /**
     * Thread-safe Singleton instance retrieval using double-checked locking
     */
    public static CacheManager getInstance() {
        if (instance == null) {
            synchronized (CacheManager.class) {
                if (instance == null) {
                    instance = new CacheManager();
                }
            }
        }
        return instance;
    }

    /**
     * Store a value in cache with a specific key
     */
    public <T> void put(String key, T value) {
        if (key == null || value == null) {
            loggingService.logWarn("Attempted to cache null key or value");
            return;
        }
        
        cache.put(key, new CacheEntry<>(value));
        loggingService.logDebug("Cached: " + key);
    }

    /**
     * Store a list in cache with a specific key
     */
    public <T> void putList(String key, List<T> value) {
        if (key == null || value == null) {
            loggingService.logWarn("Attempted to cache null key or list");
            return;
        }
        
        cache.put(key, new CacheEntry<>(new ArrayList<>(value)));
        loggingService.logDebug("Cached list: " + key + " (size: " + value.size() + ")");
    }

    /**
     * Retrieve a value from cache
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, Class<T> type) {
        if (key == null) {
            return Optional.empty();
        }

        CacheEntry<?> entry = cache.get(key);
        if (entry != null) {
            loggingService.logDebug("Cache HIT: " + key);
            return Optional.ofNullable((T) entry.getValue());
        }
        
        loggingService.logDebug("Cache MISS: " + key);
        return Optional.empty();
    }

    /**
     * Retrieve a list from cache
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<List<T>> getList(String key) {
        if (key == null) {
            return Optional.empty();
        }

        CacheEntry<?> entry = cache.get(key);
        if (entry != null && entry.getValue() instanceof List) {
            loggingService.logDebug("Cache HIT (list): " + key);
            // Return a copy to maintain immutability
            List<T> originalList = (List<T>) entry.getValue();
            return Optional.of(new ArrayList<>(originalList));
        }
        
        loggingService.logDebug("Cache MISS (list): " + key);
        return Optional.empty();
    }

    /**
     * Invalidate (remove) a specific cache entry
     */
    public void invalidate(String key) {
        if (key != null && cache.remove(key) != null) {
            loggingService.logInfo("Cache invalidated: " + key);
        }
    }

    /**
     * Invalidate all cache entries matching a pattern
     * Example: invalidate("media:*") removes all media-related cache entries
     */
    public void invalidatePattern(String pattern) {
        if (pattern == null) {
            return;
        }

        String regex = pattern.replace("*", ".*");
        List<String> keysToRemove = new ArrayList<>();
        
        for (String key : cache.keySet()) {
            if (key.matches(regex)) {
                keysToRemove.add(key);
            }
        }

        keysToRemove.forEach(cache::remove);
        
        if (!keysToRemove.isEmpty()) {
            loggingService.logInfo("Cache invalidated (pattern): " + pattern + 
                                   " (" + keysToRemove.size() + " entries removed)");
        }
    }

    /**
     * Clear all cache entries
     */
    public void clearAll() {
        int size = cache.size();
        cache.clear();
        loggingService.logInfo("Cache cleared: " + size + " entries removed");
    }

    /**
     * Check if a key exists in cache
     */
    public boolean containsKey(String key) {
        return key != null && cache.containsKey(key);
    }

    /**
     * Get cache statistics
     */
    public CacheStats getStats() {
        return new CacheStats(cache.size(), cache.keySet());
    }

    /**
     * Inner class to wrap cached values with metadata
     */
    private static class CacheEntry<T> {
        private final T value;
        private final LocalDateTime createdAt;

        public CacheEntry(T value) {
            this.value = value;
            this.createdAt = LocalDateTime.now();
        }

        public T getValue() {
            return value;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }

    /**
     * Cache statistics class
     */
    public static class CacheStats {
        private final int size;
        private final Set<String> keys;

        public CacheStats(int size, Set<String> keys) {
            this.size = size;
            this.keys = new HashSet<>(keys);
        }

        public int getSize() {
            return size;
        }

        public Set<String> getKeys() {
            return new HashSet<>(keys);
        }

        @Override
        public String toString() {
            return String.format("CacheStats{size=%d, keys=%s}", size, keys);
        }
    }
}
