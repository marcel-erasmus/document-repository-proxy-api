package com.voidworks.drp.exception.document;

import com.voidworks.drp.exception.DocumentRepositoryException;

public class DocumentUploadException extends DocumentRepositoryException {

   public DocumentUploadException(Exception e) {
      super("Document upload failed!", e);
   }

}
