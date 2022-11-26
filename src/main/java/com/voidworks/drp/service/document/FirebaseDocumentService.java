package com.voidworks.drp.service.document;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.*;
import com.voidworks.drp.config.storage.firebase.FirebaseConfig;
import com.voidworks.drp.enums.storage.StorageProvider;
import com.voidworks.drp.exception.document.DocumentDeleteException;
import com.voidworks.drp.exception.document.DocumentUploadException;
import com.voidworks.drp.exception.storage.StorageProviderConfigurationException;
import com.voidworks.drp.model.service.DocumentPutRequestBean;
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

            File file = getFile(documentPutRequestBean.getFile(), documentPutRequestBean.getKey());

            storage.create(blobInfo, Files.readAllBytes(file.toPath()));

            if (file.delete()) {
                log.debug("Successfully deleted temporary file [{}] after upload to Firebase!", documentPutRequestBean.getKey());
            } else {
                log.debug("Deletion of temporary file [{}] after upload to Firebase failed! It might need to be manually deleted!", documentPutRequestBean.getKey());
            }
        } catch (StorageProviderConfigurationException e) {
            throw e;
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
            log.error("Error creating temporary file [{}] for upload to Firebase! {}", filename, e.getMessage(), e);

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

    private Storage getStorage() throws StorageProviderConfigurationException {
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

            throw new StorageProviderConfigurationException(StorageProvider.FIREBASE.name(), e);
        }

        Storage storage = storageOptions.getService();

        log.debug("Firebase client instantiated! Took [{}] ms.", System.currentTimeMillis() - timeStarted);

        return storage;
    }

}
