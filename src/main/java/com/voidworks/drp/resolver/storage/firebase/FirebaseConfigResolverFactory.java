package com.voidworks.drp.resolver.storage.firebase;

import com.voidworks.drp.enums.storage.StorageProvider;
import com.voidworks.drp.enums.storage.StorageProviderConfigResolverType;
import com.voidworks.drp.exception.storage.StorageProviderConfigurationException;
import com.voidworks.drp.model.service.StorageProviderBean;

public class FirebaseConfigResolverFactory {

   public static FirebaseConfigResolver getInstance(StorageProviderBean storageProviderBean) {
      if ( !storageProviderBean.getStorageProvider().equals(StorageProvider.FIREBASE) ) {
         throw new StorageProviderConfigurationException(storageProviderBean.getId());
      }

      if (storageProviderBean.getConfigResolverType().equals(StorageProviderConfigResolverType.FIREBASE_FILE_PROPERTY)) {
         return PropertyFirebaseConfigResolver.builder()
                 .id(storageProviderBean.getId())
                 .propertySource(storageProviderBean.getResolverConfig().get("propertySource"))
                 .build();
      }

      throw new StorageProviderConfigurationException(storageProviderBean.getId());
   }

}
