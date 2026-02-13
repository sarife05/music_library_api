package kz.aitu.music_library_api.service.interfaces;

import java.util.List;
import java.util.Optional;

/**
 * Cache Service Interface
 * Provides abstraction for caching operations following Interface Segregation Principle
 */
public interface CacheService {
    
    /**
     * Cache a single object
     */
    <T> void cache(String key, T value);
    
    /**
     * Cache a list of objects
     */
    <T> void cacheList(String key, List<T> value);
    
    /**
     * Retrieve a cached object
     */
    <T> Optional<T> getCached(String key, Class<T> type);
    
    /**
     * Retrieve a cached list
     */
    <T> Optional<List<T>> getCachedList(String key);
    
    /**
     * Invalidate a specific cache entry
     */
    void invalidate(String key);
    
    /**
     * Invalidate all entries matching a pattern
     */
    void invalidatePattern(String pattern);
    
    /**
     * Clear all cache
     */
    void clearCache();
    
    /**
     * Check if key exists in cache
     */
    boolean isCached(String key);
}
