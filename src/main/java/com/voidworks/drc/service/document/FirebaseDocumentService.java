package com.voidworks.drc.service.document;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.*;
import com.voidworks.drc.config.storage.firebase.FirebaseConfig;
import com.voidworks.drc.exception.DocumentDeleteException;
import com.voidworks.drc.exception.DocumentUploadException;
import com.voidworks.drc.exception.StorageProviderConfigurationException;
import com.voidworks.drc.model.service.DocumentPutRequestBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final FirebaseConfig firebaseConfig;

    @Autowired
    public FirebaseDocumentService(FirebaseConfig firebaseConfig) {
        this.firebaseConfig = firebaseConfig;
    }

    @Override
    public void uploadDocument(DocumentPutRequestBean documentPutRequestBean) {
        try {
            String bucket = firebaseConfig.getBucket();

            Storage storage = getStorage();

            BlobId blobId = BlobId.of(bucket, documentPutRequestBean.getKey());

            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(documentPutRequestBean.getContentType())
                    .build();

            File file = getFile(documentPutRequestBean.getFile(), documentPutRequestBean.getFilename());

            storage.create(blobInfo, Files.readAllBytes(file.toPath()));

            if ( file.delete() ) {
                log.info("Successfully deleted temporary file for upload to Firebase!");
            } else {
                log.info("Deletion of temporary file for upload to Firebase failed! It might need to be manually deleted!");
            }
        } catch (Exception e) {
            log.error("Failed to upload document to Firebase! {}", e.getMessage(), e);

            throw new DocumentUploadException(e);
        }
    }

    private File getFile(InputStream inputStream, String filename) {
        File file = new File(filename);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(inputStream.readAllBytes());
        } catch (Exception e) {
            log.error("Error creating temporary file for upload to Firebase! {}", e.getMessage(), e);

            throw new DocumentUploadException(e);
        }

        return file;
    }

    @Override
    public void deleteDocument(String key) {
        try {
            String bucket = firebaseConfig.getBucket();

            Storage storage = getStorage();

            BlobId blobId = BlobId.of(bucket, key);

            storage.delete(blobId);
        } catch (Exception e) {
            log.error("Could not delete file from S3! {}", e.getMessage(), e);

            throw new DocumentDeleteException(e);
        }
    }

    @Override
    public InputStream downloadDocument(String key) {
        String bucket = firebaseConfig.getBucket();

        Storage storage = getStorage();

        Blob blob = storage.get(BlobId.of(bucket, key));

        ReadChannel reader = blob.reader();

        return Channels.newInputStream(reader);
    }

    private Storage getStorage() {
        String projectId = firebaseConfig.getProjectId();

        long timeStarted = System.currentTimeMillis();

        StorageOptions storageOptions;
        try (FileInputStream serviceAccount = new FileInputStream(firebaseConfig.getServiceAccount())) {
            storageOptions = StorageOptions.newBuilder()
                    .setProjectId(projectId)
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            throw new StorageProviderConfigurationException(e.getMessage());
        }

        Storage storage = storageOptions.getService();

        log.info("Firebase client instantiated! Took [{}] ms.", System.currentTimeMillis() - timeStarted);

        return storage;
    }

}
