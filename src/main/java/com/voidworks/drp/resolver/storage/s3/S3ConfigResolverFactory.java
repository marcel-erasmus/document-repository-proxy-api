package com.voidworks.drp.resolver.storage.s3;

import com.voidworks.drp.enums.storage.StorageProvider;
import com.voidworks.drp.enums.storage.StorageProviderConfigResolverType;
import com.voidworks.drp.exception.storage.StorageProviderConfigurationException;
import com.voidworks.drp.model.service.StorageProviderBean;

public class S3ConfigResolverFactory {

   public static S3ConfigResolver getInstance(StorageProviderBean storageProviderBean) {
      if ( !storageProviderBean.getStorageProvider().equals(StorageProvider.S3) ) {
         throw new StorageProviderConfigurationException(storageProviderBean.getId());
      }

      if (storageProviderBean.getConfigResolverType().equals(StorageProviderConfigResolverType.S3_FILE_PROPERTY)) {
         return PropertyS3ConfigResolver.builder()
                 .id(storageProviderBean.getId())
                 .propertySource(storageProviderBean.getResolverConfig().get("propertySource"))
                 .build();
      }

      throw new StorageProviderConfigurationException(storageProviderBean.getId());
   }

}