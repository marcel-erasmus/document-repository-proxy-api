package com.voidworks.drc.exception.api;

import com.voidworks.drc.exception.DocumentRepositoryException;

public class MalformedRequestBodyException extends DocumentRepositoryException {

   public MalformedRequestBodyException() {
      super("Malformed request body.");
   }

}
