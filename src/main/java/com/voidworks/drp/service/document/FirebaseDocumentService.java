package com.voidworks.drp.service.document;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.*;
import com.voidworks.drp.exception.document.DocumentDeleteException;
import com.voidworks.drp.exception.document.DocumentUploadException;
import com.voidworks.drp.exception.storage.StorageProviderConfigurationException;
import com.voidworks.drp.model.config.FirebaseConfig;
import com.voidworks.drp.model.document.DocumentIdentity;
import com.voidworks.drp.model.service.DocumentPutRequestBean;
import com.voidworks.drp.resolver.storage.firebase.FirebaseConfigResolverFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.file.Files;

@Slf4j
@RequestScope
@Service
public class FirebaseDocumentService implements DocumentService {

    @Override
    public void uploadDocument(DocumentPutRequestBean documentPutRequestBean) {
        FirebaseConfig firebaseConfig = FirebaseConfigResolverFactory
                .getInstance(documentPutRequestBean.getStorageProvider())
                .resolve();

        Storage storage = getStorage(firebaseConfig);

        File file = getFile(documentPutRequestBean.getFile(), documentPutRequestBean.getDocumentSource().key());
        try {
            BlobId blobId = BlobId.of(firebaseConfig.getBucket(), documentPutRequestBean.getDocumentSource().key());

            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(documentPutRequestBean.getContentType())
                    .build();

            storage.create(blobInfo, Files.readAllBytes(file.toPath()));
        } catch (Exception e) {
            log.error("Failed to upload document to Firebase! {}", e.getMessage(), e);

            throw new DocumentUploadException(e);
        } finally {
            if (file.delete()) {
                log.debug("Successfully deleted temporary file [{}] after upload to Firebase!", documentPutRequestBean.getDocumentSource().key());
            } else {
                log.debug("Deletion of temporary file [{}] after upload to Firebase failed! It might need to be manually deleted!", documentPutRequestBean.getDocumentSource().key());
            }
        }
    }

    private File getFile(InputStream inputStream, String filename) {
        File file = new File(filename);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(inputStream.readAllBytes());
        } catch (Exception e) {
            log.error("Error creating temporary file [{}] for upload to Firebase! {}", filename, e.getMessage(), e);

            throw new DocumentUploadException(e);
        }

        return file;
    }

    @Override
    public void deleteDocument(DocumentIdentity documentIdentity) {
        FirebaseConfig firebaseConfig = FirebaseConfigResolverFactory
                .getInstance(documentIdentity.storageProviderBean())
                .resolve();

        Storage storage = getStorage(firebaseConfig);

        try {
            BlobId blobId = BlobId.of(firebaseConfig.getBucket(), documentIdentity.documentSource().key());

            storage.delete(blobId);
        } catch (Exception e) {
            log.error("Could not delete file from S3! {}", e.getMessage(), e);

            throw new DocumentDeleteException(e);
        }
    }

    @Override
    public InputStream downloadDocument(DocumentIdentity documentIdentity) {
        FirebaseConfig firebaseConfig = FirebaseConfigResolverFactory
                .getInstance(documentIdentity.storageProviderBean())
                .resolve();

        Storage storage = getStorage(firebaseConfig);

        Blob blob = storage.get(BlobId.of(firebaseConfig.getBucket(), documentIdentity.documentSource().key()));

        ReadChannel reader = blob.reader();

        return Channels.newInputStream(reader);
    }

    private Storage getStorage(FirebaseConfig firebaseConfig) throws StorageProviderConfigurationException {
        String projectId = firebaseConfig.getProjectId();

        StorageOptions storageOptions;
        try (FileInputStream serviceAccount = new FileInputStream(firebaseConfig.getServiceAccount())) {
            storageOptions = StorageOptions.newBuilder()
                    .setProjectId(projectId)
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            throw new StorageProviderConfigurationException(firebaseConfig.getId(), e);
        }

        return storageOptions.getService();
    }

}
