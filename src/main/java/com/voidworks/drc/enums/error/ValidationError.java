package com.voidworks.drc.enums.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ValidationError {

   REQUIRED("This is a required field.");

   private final String message;

}
