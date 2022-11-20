package com.voidworks.drc.exception;

public class DatabasePersistenceException extends DocumentRepositoryException {

    public DatabasePersistenceException(String referenceId, String message) {
        super(referenceId, message);
    }

}
