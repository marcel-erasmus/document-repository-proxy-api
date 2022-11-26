package com.voidworks.drp.resolver.storage.s3;

import com.voidworks.drp.exception.storage.StorageProviderConfigurationException;
import com.voidworks.drp.model.config.S3Config;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Builder
public class PropertyS3ConfigResolver implements S3ConfigResolver {

   private static final String PROPERTY_BUCKET = "storage.s3.bucket";
   private static final String PROPERTY_REGION = "storage.s3.region";
   private static final String PROPERTY_ACCESS_KEY_ID = "storage.s3.access-key-id";
   private static final String PROPERTY_SECRET_ACCESS_KEY = "storage.s3.secret-access-key";

   private String id;
   private String propertySource;

   @Override
   public S3Config resolve() {
      File file = new File(propertySource);
      try (InputStream inputStream = new FileInputStream(file)) {
         Properties properties = new Properties();
         properties.load(inputStream);

         return new S3Config(
                 properties.getProperty(PROPERTY_BUCKET),
                 properties.getProperty(PROPERTY_REGION),
                 properties.getProperty(PROPERTY_ACCESS_KEY_ID),
                 properties.getProperty(PROPERTY_SECRET_ACCESS_KEY)
         );
      } catch (Exception e) {
         log.error("Error in resolving storage provider config for Firebase! {}", e.getMessage(), e);

         throw new StorageProviderConfigurationException(id, e);
      }
   }

}
