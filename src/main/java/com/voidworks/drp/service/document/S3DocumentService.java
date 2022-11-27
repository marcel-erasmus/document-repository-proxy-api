package com.voidworks.drp.service.document;

import com.voidworks.drp.exception.document.DocumentDeleteException;
import com.voidworks.drp.exception.document.DocumentUploadException;
import com.voidworks.drp.exception.storage.StorageProviderConfigurationException;
import com.voidworks.drp.model.config.S3Config;
import com.voidworks.drp.model.document.DocumentIdentity;
import com.voidworks.drp.model.service.DocumentPutRequestBean;
import com.voidworks.drp.resolver.storage.s3.S3ConfigResolverFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
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

    @Override
    public void uploadDocument(DocumentPutRequestBean documentPutRequestBean) {
        S3Config s3Config = S3ConfigResolverFactory
                .getInstance(documentPutRequestBean.getStorageProvider())
                .resolve();

        S3Client s3Client = getS3Client(s3Config);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Config.getBucket())
                    .key(documentPutRequestBean.getDocumentSource().key())
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
    public void deleteDocument(DocumentIdentity documentIdentity) {
        S3Config s3Config = S3ConfigResolverFactory
                .getInstance(documentIdentity.storageProviderBean())
                .resolve();

        S3Client s3Client = getS3Client(s3Config);

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(s3Config.getBucket())
                    .key(documentIdentity.documentSource().key())
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (StorageProviderConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new DocumentDeleteException(e);
        }
    }

    @Override
    public InputStream downloadDocument(DocumentIdentity documentIdentity) {
        S3Config s3Config = S3ConfigResolverFactory
                .getInstance(documentIdentity.storageProviderBean())
                .resolve();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Config.getBucket())
                .key(documentIdentity.documentSource().key())
                .build();

        S3Client s3Client = getS3Client(s3Config);

        return s3Client.getObject(getObjectRequest);
    }

    private S3Client getS3Client(S3Config s3Config) {
        try {
            AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(
                    s3Config.getAccessKeyId(),
                    s3Config.getSecretAccessKey()
            );

            return S3Client.builder()
                    .region(Region.of(s3Config.getRegion()))
                    .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                    .build();
        } catch (Exception e) {
            throw new StorageProviderConfigurationException(s3Config.getId(), e);
        }
    }

}
