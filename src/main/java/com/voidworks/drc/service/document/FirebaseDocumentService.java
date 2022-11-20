package com.voidworks.drc.service.document;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.voidworks.drc.config.storage.firebase.FirebaseConfig;
import com.voidworks.drc.exception.StorageProviderConfigurationException;
import com.voidworks.drc.model.service.DocumentPutRequestBean;
import jdk.jshell.spi.ExecutionControl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.UUID;

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
    public void uploadDocument(DocumentPutRequestBean documentPutRequestBean) throws Exception {
        throw new ExecutionControl.NotImplementedException("Not yet implemented for Firebase!");
    }

    @Override
    public InputStream downloadDocument(String key) {
        String projectId = firebaseConfig.getProjectId();

        String bucket = firebaseConfig.getBucket();

        StorageOptions storageOptions;
        try (FileInputStream serviceAccount = new FileInputStream(firebaseConfig.getServiceAccount())) {
            storageOptions = StorageOptions.newBuilder()
                    .setProjectId(projectId)
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            throw new StorageProviderConfigurationException(UUID.randomUUID().toString(), e.getMessage());
        }

        Storage storage = storageOptions.getService();

        Blob blob = storage.get(BlobId.of(bucket, key));

        ReadChannel reader = blob.reader();

        return Channels.newInputStream(reader);
    }

}
