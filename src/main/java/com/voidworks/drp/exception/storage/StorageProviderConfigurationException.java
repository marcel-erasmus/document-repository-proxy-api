package com.voidworks.drp.exception.storage;

import com.voidworks.drp.exception.DocumentRepositoryException;
import lombok.Getter;

@Getter
public class StorageProviderConfigurationException extends DocumentRepositoryException {

    private final String storageProvider;

    public StorageProviderConfigurationException(String storageProvider) {
        this(storageProvider, null);
    }

    public StorageProviderConfigurationException(String storageProvider, Exception e) {
        super(String.format("Error with storage provider configuration for [%s]!", storageProvider), e);

        this.storageProvider = storageProvider;
    }

}
