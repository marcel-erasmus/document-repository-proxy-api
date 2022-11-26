package com.voidworks.drp.resolver.storage.firebase;

import com.voidworks.drp.exception.storage.StorageProviderConfigurationException;
import com.voidworks.drp.model.config.FirebaseConfig;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Builder
public class PropertyFirebaseConfigResolver implements FirebaseConfigResolver {

    private static final String PROPERTY_SERVICE_ACCOUNT = "storage.firebase.service-account";
    private static final String PROPERTY_PROJECT_ID = "storage.firebase.project-id";
    private static final String PROPERTY_BUCKET = "storage.firebase.bucket";

    private String id;
    private String propertySource;

    @Override
    public FirebaseConfig resolve() {
        File file = new File(propertySource);
        try (InputStream inputStream = new FileInputStream(file)) {
            Properties properties = new Properties();
            properties.load(inputStream);

            return new FirebaseConfig(
                    properties.getProperty(PROPERTY_SERVICE_ACCOUNT),
                    properties.getProperty(PROPERTY_PROJECT_ID),
                    properties.getProperty(PROPERTY_BUCKET)
            );
        } catch (Exception e) {
            log.error("Error in resolving storage provider config for Firebase! {}", e.getMessage(), e);

            throw new StorageProviderConfigurationException(id, e);
        }
    }

}
