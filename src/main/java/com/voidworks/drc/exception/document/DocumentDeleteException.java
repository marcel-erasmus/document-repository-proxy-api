package com.voidworks.drc.exception.document;

import com.voidworks.drc.exception.DocumentRepositoryException;

public class DocumentDeleteException extends DocumentRepositoryException {

   public DocumentDeleteException(Exception e) {
      super("Unable to delete document!", e);
   }

}
