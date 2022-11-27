package com.voidworks.drp.model.database.storage;

import com.voidworks.drp.enums.storage.StorageProvider;
import com.voidworks.drp.enums.storage.StorageProviderConfigResolverType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@Document("storage-providers")
public class StorageProviderDocument {

    @Id
    private String id;
    private StorageProvider storageProvider;
    private StorageProviderConfigResolverType configResolverType;
    private Map<String, String> resolverConfig;

}
