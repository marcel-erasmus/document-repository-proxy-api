package com.voidworks.drp.exception.document;

import com.voidworks.drp.exception.DocumentRepositoryException;

public class DocumentDeleteException extends DocumentRepositoryException {

   public DocumentDeleteException(Exception e) {
      super("Unable to delete document!", e);
   }

}
