package com.voidworks.drc.exception.storage;

import com.voidworks.drc.exception.DocumentRepositoryException;
import lombok.Getter;

@Getter
public class StorageProviderNotSupportedException extends DocumentRepositoryException {

    private final String storageProvider;

    public StorageProviderNotSupportedException(String storageProvider) {
        super(String.format("Storage provider [%s] not supported.", storageProvider));

        this.storageProvider = storageProvider;
    }

}
