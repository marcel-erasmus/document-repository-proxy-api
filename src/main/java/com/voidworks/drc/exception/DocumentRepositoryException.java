package com.voidworks.drc.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class DocumentRepositoryException extends RuntimeException {

    protected final String referenceId;
    protected final String message;

    DocumentRepositoryException(String referenceId, String message) {
        super(String.format("Reference ID: %s, Message: %s", referenceId, message));

        log.error("Reference ID: {}, Message: {}", referenceId, message);

        this.referenceId = referenceId;
        this.message = message;
    }

}
