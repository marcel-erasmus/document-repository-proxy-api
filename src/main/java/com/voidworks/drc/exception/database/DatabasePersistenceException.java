package com.voidworks.drc.exception.database;

import com.voidworks.drc.exception.DocumentRepositoryException;

public class DatabasePersistenceException extends DocumentRepositoryException {

    public DatabasePersistenceException(String message, Exception e) {
        super(message, e);
    }

}
