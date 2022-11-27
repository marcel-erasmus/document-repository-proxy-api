package com.voidworks.drp.model.config;

public record FirebaseConfig(String id,
                             String serviceAccount,
                             String projectId,
                             String bucket) implements StorageProviderIdentity {

}
