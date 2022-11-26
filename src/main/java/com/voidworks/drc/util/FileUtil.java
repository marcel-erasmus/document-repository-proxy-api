package com.voidworks.drc.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.NONE)
public class FileUtil {

   public static String getFileExtension(String filename) {
      return Optional.ofNullable(filename)
               .filter(f -> f.contains("."))
               .map(f -> f.substring(filename.lastIndexOf(".")))
               .orElse("");
   }

}
