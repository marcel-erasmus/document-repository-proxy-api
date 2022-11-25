package com.voidworks.drc.exception.metadata;

import com.voidworks.drc.exception.DocumentRepositoryException;

public class DocumentMetadataNotFoundException extends DocumentRepositoryException {

    public DocumentMetadataNotFoundException(String id) {
        super(String.format("Document metadata with ID [%s] not found.", id));
    }

}
