package com.voidworks.drc.service.document;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.voidworks.drc.config.storage.firebase.FirebaseConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.channels.Channels;

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
    public InputStream downloadDocument(String key) throws Exception {
        String projectId = firebaseConfig.getProjectId();

        String bucket = firebaseConfig.getBucket();

        FileInputStream serviceAccount = new FileInputStream(firebaseConfig.getServiceAccount());

        StorageOptions storageOptions = StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        Storage storage = storageOptions.getService();

        Blob blob = storage.get(BlobId.of(bucket, key));

        ReadChannel reader = blob.reader();

        return  Channels.newInputStream(reader);
    }

}
