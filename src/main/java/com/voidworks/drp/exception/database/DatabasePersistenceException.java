package com.voidworks.drp.exception.database;

import com.voidworks.drp.exception.DocumentRepositoryException;

public class DatabasePersistenceException extends DocumentRepositoryException {

    public DatabasePersistenceException(String message, Exception e) {
        super(message, e);
    }

}
