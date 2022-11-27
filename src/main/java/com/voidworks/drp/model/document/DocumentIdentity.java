package com.voidworks.drp.model.document;

import com.voidworks.drp.model.service.StorageProviderBean;

public record DocumentIdentity(String id,
                               StorageProviderBean storageProviderBean,
                               DocumentSource documentSource) {

}
