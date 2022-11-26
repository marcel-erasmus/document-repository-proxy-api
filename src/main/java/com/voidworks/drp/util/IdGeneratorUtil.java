package com.voidworks.drp.util;

import java.util.UUID;

public class IdGeneratorUtil {

   public static String getId() {
      return UUID.randomUUID().toString().replaceAll("-", "");
   }

}
