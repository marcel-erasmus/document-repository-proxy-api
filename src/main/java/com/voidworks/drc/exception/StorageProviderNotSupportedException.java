package com.voidworks.drc.exception;

public class StorageProviderNotSupportedException extends DocumentRepositoryException {

    public StorageProviderNotSupportedException(String storageProvider) {
        super(String.format("Storage provider [%s] not supported.", storageProvider));
    }

}
