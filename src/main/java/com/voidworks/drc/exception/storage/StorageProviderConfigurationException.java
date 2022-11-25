package com.voidworks.drc.exception.storage;

import com.voidworks.drc.exception.DocumentRepositoryException;
import lombok.Getter;

@Getter
public class StorageProviderConfigurationException extends DocumentRepositoryException {

    private final String storageProvider;

    public StorageProviderConfigurationException(String storageProvider, Exception e) {
        super(String.format("Error with storage provider configuration for [%s]!", storageProvider), e);

        this.storageProvider = storageProvider;
    }

}
