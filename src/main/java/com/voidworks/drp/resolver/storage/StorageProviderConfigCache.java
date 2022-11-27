package com.voidworks.drp.resolver.storage;

import com.voidworks.drp.model.config.FirebaseConfig;
import com.voidworks.drp.model.config.S3Config;
import com.voidworks.drp.model.config.StorageProviderIdentity;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class StorageProviderConfigCache {

    private static final Map<String, Object> CACHE = new ConcurrentHashMap<>();

    public static <T extends StorageProviderIdentity> void putStorageProviderConfig(T storageProviderConfig) {
        log.debug("Caching storage provider config for [{}].", storageProviderConfig.id());

        CACHE.put(storageProviderConfig.id(), storageProviderConfig);
    }

    private static Optional<Object> getStorageProviderConfig(String id) {
        if ( !CACHE.containsKey(id) ) {
            log.debug("Cache miss for storage provider [{}]!", id);

            return Optional.empty();
        }

        log.debug("Cache hit for storage provider [{}]!", id);

        return Optional.of(CACHE.get(id));
    }

    public static Optional<FirebaseConfig> getFirebaseConfig(String id) {
        Optional<Object> storageProviderConfig = getStorageProviderConfig(id);

        if (storageProviderConfig.isEmpty()) {
            return Optional.empty();
        }

        FirebaseConfig firebaseConfig = (FirebaseConfig) storageProviderConfig.get();

        return Optional.of(firebaseConfig);
    }

    public static Optional<S3Config> getS3Config(String id) {
        Optional<Object> storageProviderConfig = getStorageProviderConfig(id);

        if (storageProviderConfig.isEmpty()) {
            return Optional.empty();
        }

        S3Config firebaseConfig = (S3Config) storageProviderConfig.get();

        return Optional.of(firebaseConfig);
    }

}
