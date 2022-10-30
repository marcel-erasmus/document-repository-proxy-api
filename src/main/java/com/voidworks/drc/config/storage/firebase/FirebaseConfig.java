package com.voidworks.drc.config.storage.firebase;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class FirebaseConfig {

    @Value("${storage.firebase.service-account}")
    private String serviceAccount;
    @Value("${storage.firebase.project-id}")
    private String projectId;
    @Value("${storage.firebase.bucket}")
    private String bucket;

}
