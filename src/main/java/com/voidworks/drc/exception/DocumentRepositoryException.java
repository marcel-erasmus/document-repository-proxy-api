package com.voidworks.drc.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@Getter
public class DocumentRepositoryException extends RuntimeException {

    protected final String referenceId;
    protected final String message;

    DocumentRepositoryException(String message) {
        super();

        this.referenceId = UUID.randomUUID().toString();
        this.message = message;

        log.error("Reference ID: {}, Message: {}", referenceId, message);
    }

}
