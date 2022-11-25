package com.voidworks.drc.exception.document;

import com.voidworks.drc.exception.DocumentRepositoryException;

public class DocumentUploadException extends DocumentRepositoryException {

   public DocumentUploadException(Exception e) {
      super("Document upload failed!", e);
   }

}
