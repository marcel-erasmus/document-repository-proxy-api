package com.voidworks.drc.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@Getter
public class DocumentRepositoryException extends RuntimeException {

    protected String referenceId;
    protected String message;
    protected Exception exception;

    public DocumentRepositoryException(String referenceId, String message, Exception exception) {
        this.referenceId = referenceId;
        this.message = message;

        if (exception != null) {
            log.error(exception.getMessage(), exception);
        }

        log.error("Reference ID: {}, Message: {}", referenceId, message);
    }

    public DocumentRepositoryException(String message, Exception exception) {
        this(UUID.randomUUID().toString(), message, exception);
    }

    public DocumentRepositoryException(String message) {
        this(UUID.randomUUID().toString(), message, null);
    }

}
