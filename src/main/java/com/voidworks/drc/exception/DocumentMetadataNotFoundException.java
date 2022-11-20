package com.voidworks.drc.exception;

public class DocumentMetadataNotFoundException extends DocumentRepositoryException {

    public DocumentMetadataNotFoundException(String id) {
        super(String.format("Document metadata with ID [%s] not found!", id));
    }

}
