package com.voidworks.drc.service.document;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.voidworks.drc.config.storage.s3.S3Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.io.InputStream;

@Slf4j
@RequestScope
@Service
public class S3DocumentService implements DocumentService {

    private S3Config s3Config;
    private AmazonS3 s3Client;

    @Autowired
    public S3DocumentService(S3Config s3Config,
                             AmazonS3 s3Client) {
        this.s3Config = s3Config;
        this.s3Client = s3Client;
    }

    @Override
    public InputStream downloadDocument(String key) {
        S3Object s3object = s3Client.getObject(s3Config.getBucket(), key);

        return s3object.getObjectContent();
    }

}
