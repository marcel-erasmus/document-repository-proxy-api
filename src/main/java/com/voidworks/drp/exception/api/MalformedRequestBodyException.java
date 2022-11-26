package com.voidworks.drp.exception.api;

import com.voidworks.drp.exception.DocumentRepositoryException;

public class MalformedRequestBodyException extends DocumentRepositoryException {

   public MalformedRequestBodyException() {
      super("Malformed request body.");
   }

}
