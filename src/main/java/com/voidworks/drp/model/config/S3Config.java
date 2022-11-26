package com.voidworks.drp.model.config;

public record S3Config(String bucket,
                       String region,
                       String accessKeyId,
                       String secretAccessKey) {

}
