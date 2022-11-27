package com.voidworks.drp.enums.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StorageProviderConfigResolverType {

    S3_FILE_PROPERTY("Fetches S3 config from a properties file."),
    FIREBASE_FILE_PROPERTY("Fetches Firebase config from a properties file.");

    private final String description;

}
