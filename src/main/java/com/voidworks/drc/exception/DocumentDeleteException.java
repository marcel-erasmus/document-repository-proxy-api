package com.voidworks.drc.exception;

public class DocumentDeleteException extends DocumentRepositoryException {

   public DocumentDeleteException(Exception e) {
      super("Unable to delete document!", e);
   }

}
