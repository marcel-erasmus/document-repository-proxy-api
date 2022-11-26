package com.voidworks.drp.exception.metadata;

import com.voidworks.drp.exception.DocumentRepositoryException;

public class DocumentMetadataNotFoundException extends DocumentRepositoryException {

    public DocumentMetadataNotFoundException(String id) {
        super(String.format("Document metadata with ID [%s] not found.", id));
    }

}
