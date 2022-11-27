package com.voidworks.drp.model.config;

public record S3Config(String id,
                       String bucket,
                       String region,
                       String accessKeyId,
                       String secretAccessKey) implements StorageProviderIdentity {

}
