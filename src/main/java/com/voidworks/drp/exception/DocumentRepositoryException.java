package com.voidworks.drp.exception;

import com.voidworks.drp.util.IdGeneratorUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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
        this(IdGeneratorUtil.getId(), message, exception);
    }

    public DocumentRepositoryException(String message) {
        this(IdGeneratorUtil.getId(), message, null);
    }

}
