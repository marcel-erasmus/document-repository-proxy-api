package com.voidworks.drc.exception;

public class DocumentUploadException extends DocumentRepositoryException {

   public DocumentUploadException(Exception e) {
      super("Document upload failed!", e);
   }

}
