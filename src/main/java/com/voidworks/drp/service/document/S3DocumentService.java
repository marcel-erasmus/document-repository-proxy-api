package com.voidworks.drp.service.document;

import com.voidworks.drp.config.storage.s3.S3Config;
import com.voidworks.drp.exception.document.DocumentDeleteException;
import com.voidworks.drp.exception.document.DocumentUploadException;
import com.voidworks.drp.model.service.DocumentPutRequestBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.InputStream;

@Slf4j
@RequestScope
@Service
public class S3DocumentService implements DocumentService {

    private final S3Config s3Config;
    private final S3Client s3Client;

    @Autowired
    public S3DocumentService(S3Config s3Config,
                             S3Client s3Client) {
        this.s3Config = s3Config;
        this.s3Client = s3Client;
    }

    @Override
    public void uploadDocument(DocumentPutRequestBean documentPutRequestBean) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Config.getBucket())
                    .key(documentPutRequestBean.getKey())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(documentPutRequestBean.getFile(), documentPutRequestBean.getFile().available()));
        } catch (S3Exception e) {
            log.error(e.awsErrorDetails().errorMessage(), e);

            throw new DocumentUploadException(e);
        } catch (Exception e) {
            log.error("Failed to upload document to S3! {}", e.getMessage(), e);

            throw new DocumentUploadException(e);
        }
    }

    @Override
    public void deleteDocument(String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(s3Config.getBucket())
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            log.error("Could not delete file from S3! {}", e.getMessage(), e);

            throw new DocumentDeleteException(e);
        }
    }

    @Override
    public InputStream downloadDocument(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Config.getBucket())
                .key(key)
                .build();

        return s3Client.getObject(getObjectRequest);
    }

}
