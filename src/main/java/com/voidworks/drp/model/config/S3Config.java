package com.voidworks.drp.model.config;

import lombok.Getter;

@Getter
public class S3Config extends StorageProviderConfig {

    private final String bucket;
    private final String region;
    private final String accessKeyId;
    private final String secretAccessKey;

    public S3Config(String id, String bucket, String region, String accessKeyId, String secretAccessKey) {
        super(id);
        this.bucket = bucket;
        this.region = region;
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
    }

}
