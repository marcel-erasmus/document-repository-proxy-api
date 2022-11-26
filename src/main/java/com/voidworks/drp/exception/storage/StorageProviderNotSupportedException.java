package com.voidworks.drp.exception.storage;

import com.voidworks.drp.exception.DocumentRepositoryException;
import lombok.Getter;

@Getter
public class StorageProviderNotSupportedException extends DocumentRepositoryException {

    private final String storageProvider;

    public StorageProviderNotSupportedException(String storageProvider) {
        super(String.format("Storage provider [%s] not supported.", storageProvider));

        this.storageProvider = storageProvider;
    }

}
