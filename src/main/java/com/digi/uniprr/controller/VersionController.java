package com.digi.uniprr.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class VersionController {

    @Value("${app.version}")
    private String appVersion;

    private final CacheManager cacheManager;

    @Autowired
    public VersionController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @GetMapping("/version")
    public Map<String, String> getVersion() {
        // Clear all caches
        clearAllCaches();

        Map<String, String> response = new HashMap<>();
        response.put("version", appVersion);
        response.put("cacheCleared", "true");
        return response;
    }

    private void clearAllCaches() {
        if (cacheManager != null) {
            for (String name : cacheManager.getCacheNames()) {
         
                Cache cache = cacheManager.getCache(name);
                if (cache != null) {
                    cache.clear(); // clears each cache
                    System.out.println("Cleared cache: " + name);
                }
            }
        }
    }
}

