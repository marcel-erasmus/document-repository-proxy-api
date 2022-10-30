package com.voidworks.drc.config.storage.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public AmazonS3 s3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(
                accessKeyId,
                secretAccessKey
        );

        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }

}
