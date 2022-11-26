package com.voidworks.drp.resolver.storage.firebase;

import com.voidworks.drp.enums.storage.StorageProvider;
import com.voidworks.drp.enums.storage.StorageProviderConfigType;
import com.voidworks.drp.exception.storage.StorageProviderConfigurationException;
import com.voidworks.drp.model.service.StorageProviderBean;

public class FirebaseConfigResolverFactory {

   public static FirebaseConfigResolver getInstance(StorageProviderBean storageProviderBean) {
      if ( !storageProviderBean.getStorageProvider().equals(StorageProvider.FIREBASE) ) {
         throw new StorageProviderConfigurationException(storageProviderBean.getId());
      }

      if (storageProviderBean.getConfigType().equals(StorageProviderConfigType.FIREBASE_FILE_PROPERTIES)) {
         return PropertyFirebaseConfigResolver.builder()
                 .id(storageProviderBean.getId())
                 .propertySource(storageProviderBean.getConfig().get("propertySource"))
                 .build();
      }

      throw new StorageProviderConfigurationException(storageProviderBean.getId());
   }

}
