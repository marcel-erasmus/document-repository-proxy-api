package com.voidworks.drp.service.document;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.*;
import com.voidworks.drp.enums.storage.StorageProvider;
import com.voidworks.drp.exception.document.DocumentDeleteException;
import com.voidworks.drp.exception.document.DocumentUploadException;
import com.voidworks.drp.exception.storage.StorageProviderConfigurationException;
import com.voidworks.drp.model.document.DocumentSource;
import com.voidworks.drp.model.config.FirebaseConfig;
import com.voidworks.drp.model.service.DocumentPutRequestBean;
import com.voidworks.drp.model.service.StorageProviderBean;
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
        try {
            FirebaseConfig firebaseConfig = FirebaseConfigResolverFactory
                    .getInstance(documentPutRequestBean.getStorageProvider())
                    .resolve();

            BlobId blobId = BlobId.of(firebaseConfig.bucket(), documentPutRequestBean.getDocumentSource().getKey());

            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(documentPutRequestBean.getContentType())
                    .build();

            File file = getFile(documentPutRequestBean.getFile(), documentPutRequestBean.getDocumentSource().getKey());

            Storage storage = getStorage(firebaseConfig);

            storage.create(blobInfo, Files.readAllBytes(file.toPath()));

            if (file.delete()) {
                log.debug("Successfully deleted temporary file [{}] after upload to Firebase!", documentPutRequestBean.getDocumentSource().getKey());
            } else {
                log.debug("Deletion of temporary file [{}] after upload to Firebase failed! It might need to be manually deleted!", documentPutRequestBean.getDocumentSource().getKey());
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
    public void deleteDocument(StorageProviderBean storageProviderBean, DocumentSource documentSource) {
        try {
            FirebaseConfig firebaseConfig = FirebaseConfigResolverFactory
                    .getInstance(storageProviderBean)
                    .resolve();

            BlobId blobId = BlobId.of(firebaseConfig.bucket(), documentSource.getKey());

            Storage storage = getStorage(firebaseConfig);

            storage.delete(blobId);
        } catch (Exception e) {
            log.error("Could not delete file from S3! {}", e.getMessage(), e);

            throw new DocumentDeleteException(e);
        }
    }

    @Override
    public InputStream downloadDocument(StorageProviderBean storageProviderBean, DocumentSource documentSource) {
        FirebaseConfig firebaseConfig = FirebaseConfigResolverFactory
                .getInstance(storageProviderBean)
                .resolve();

        Storage storage = getStorage(firebaseConfig);

        Blob blob = storage.get(BlobId.of(firebaseConfig.bucket(), documentSource.getKey()));

        ReadChannel reader = blob.reader();

        return Channels.newInputStream(reader);
    }

    private Storage getStorage(FirebaseConfig firebaseConfig) throws StorageProviderConfigurationException {
        String projectId = firebaseConfig.projectId();

        long timeStarted = System.currentTimeMillis();

        StorageOptions storageOptions;
        try (FileInputStream serviceAccount = new FileInputStream(firebaseConfig.serviceAccount())) {
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
