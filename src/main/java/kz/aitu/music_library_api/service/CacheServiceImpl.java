package kz.aitu.music_library_api.service;

import kz.aitu.music_library_api.patterns.CacheManager;
import kz.aitu.music_library_api.service.interfaces.CacheService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Cache Service Implementation
 * Delegates cache operations to the Singleton CacheManager
 * Follows Dependency Inversion Principle by depending on abstractions
 */
@Service
public class CacheServiceImpl implements CacheService {

    private final CacheManager cacheManager;

    public CacheServiceImpl() {
        this.cacheManager = CacheManager.getInstance();
    }

    @Override
    public <T> void cache(String key, T value) {
        cacheManager.put(key, value);
    }

    @Override
    public <T> void cacheList(String key, List<T> value) {
        cacheManager.putList(key, value);
    }

    @Override
    public <T> Optional<T> getCached(String key, Class<T> type) {
        return cacheManager.get(key, type);
    }

    @Override
    public <T> Optional<List<T>> getCachedList(String key) {
        return cacheManager.getList(key);
    }

    @Override
    public void invalidate(String key) {
        cacheManager.invalidate(key);
    }

    @Override
    public void invalidatePattern(String pattern) {
        cacheManager.invalidatePattern(pattern);
    }

    @Override
    public void clearCache() {
        cacheManager.clearAll();
    }

    @Override
    public boolean isCached(String key) {
        return cacheManager.containsKey(key);
    }
}
