package com.voidworks.drp.model.config;

import lombok.Getter;

@Getter
public class FirebaseConfig extends StorageProviderConfig {

    private final String serviceAccount;
    private final String projectId;
    private final String bucket;

    public FirebaseConfig(String id, String serviceAccount, String projectId, String bucket) {
        super(id);
        this.serviceAccount = serviceAccount;
        this.projectId = projectId;
        this.bucket = bucket;
    }

}
