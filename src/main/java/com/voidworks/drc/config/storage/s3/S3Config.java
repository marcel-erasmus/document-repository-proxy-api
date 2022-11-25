package com.voidworks.drc.config.storage.s3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Configuration
public class S3Config {

    @Value("${storage.s3.access-key-id}")
    private String accessKeyId;
    @Value("${storage.s3.secret-access-key}")
    private String secretAccessKey;
    @Value("${storage.s3.region}")
    private String region;
    @Value("${storage.s3.bucket}")
    private String bucket;

    public String getBucket() {
        return bucket;
    }

    @Bean
    public S3Client s3Client() {
        long timeStarted = System.currentTimeMillis();

        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(
                accessKeyId,
                secretAccessKey
        );

        S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .build();

        log.debug("S3 client instantiated! Took [{}] ms.", System.currentTimeMillis() - timeStarted);

        return s3Client;
    }

}
