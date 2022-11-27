package com.voidworks.drp.model.service;

import com.voidworks.drp.enums.storage.StorageProvider;
import com.voidworks.drp.enums.storage.StorageProviderConfigResolverType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class StorageProviderBean {

    private String id;
    private StorageProvider storageProvider;
    private StorageProviderConfigResolverType configResolverType;
    private Map<String, String> resolverConfig;

}
