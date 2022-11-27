package com.voidworks.drp.resolver.storage;

import com.voidworks.drp.model.config.StorageProviderConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class StorageProviderConfigCache {

    private static final Map<String, StorageProviderConfig> CACHE = new ConcurrentHashMap<>();

    public static <T extends StorageProviderConfig> void  putStorageProviderConfig(T storageProviderConfig) {
        log.trace("Caching storage provider config for [{}].", storageProviderConfig.getId());

        CACHE.put(storageProviderConfig.getId(), storageProviderConfig);
    }

    public static Optional<StorageProviderConfig> getStorageProviderConfig(String id) {
        if ( !CACHE.containsKey(id) ) {
            log.trace("Cache miss for storage provider [{}]!", id);

            return Optional.empty();
        }

        log.trace("Cache hit for storage provider [{}]!", id);

        return Optional.of(CACHE.get(id));
    }

}
