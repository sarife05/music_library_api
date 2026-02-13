package kz.aitu.music_library_api.controller;

import kz.aitu.music_library_api.dto.ApiResponse;
import kz.aitu.music_library_api.patterns.CacheManager;
import kz.aitu.music_library_api.service.interfaces.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Cache Management Controller
 * Provides endpoints for manual cache management and statistics
 */
@RestController
@RequestMapping("/api/cache")
@CrossOrigin(origins = "*")
public class CacheController {

    private final CacheService cacheService;

    @Autowired
    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * Clear all cache entries
     * Endpoint: DELETE /api/cache/clear
     */
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<String>> clearAllCache() {
        cacheService.clearCache();
        return ResponseEntity.ok(ApiResponse.success("All cache cleared successfully"));
    }

    /**
     * Invalidate specific cache entry
     * Endpoint: DELETE /api/cache/invalidate/{key}
     */
    @DeleteMapping("/invalidate/{key}")
    public ResponseEntity<ApiResponse<String>> invalidateCache(@PathVariable String key) {
        cacheService.invalidate(key);
        return ResponseEntity.ok(ApiResponse.success("Cache invalidated: " + key));
    }

    /**
     * Invalidate cache entries by pattern
     * Endpoint: DELETE /api/cache/invalidate-pattern?pattern=media:*
     */
    @DeleteMapping("/invalidate-pattern")
    public ResponseEntity<ApiResponse<String>> invalidateCachePattern(@RequestParam String pattern) {
        cacheService.invalidatePattern(pattern);
        return ResponseEntity.ok(ApiResponse.success("Cache invalidated with pattern: " + pattern));
    }

    /**
     * Check if a key exists in cache
     * Endpoint: GET /api/cache/exists/{key}
     */
    @GetMapping("/exists/{key}")
    public ResponseEntity<ApiResponse<Boolean>> cacheExists(@PathVariable String key) {
        boolean exists = cacheService.isCached(key);
        return ResponseEntity.ok(ApiResponse.success("Cache key exists: " + key, exists));
    }

    /**
     * Get cache statistics
     * Endpoint: GET /api/cache/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<CacheManager.CacheStats>> getCacheStats() {
        CacheManager.CacheStats stats = CacheManager.getInstance().getStats();
        return ResponseEntity.ok(ApiResponse.success("Cache statistics retrieved", stats));
    }
}
