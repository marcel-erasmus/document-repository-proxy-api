package com.voidworks.drp.enums.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum ContentType {

   CSV("text/csv", ".csv"),
   DOC("application/msword", ".doc"),
   DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx"),
   GIF("image/gif", ".gif"),
   JPG("image/jpeg", ".jpg"),
   JPEG("image/jpeg", ".jpeg"),
   JSON("application/json", ".json"),
   PNG("image/png", ".png"),
   ODT("application/vnd.oasis.opendocument.text", ".odt"),
   PDF("application/pdf", ".pdf"),
   PPT("application/vnd.ms-powerpoint", ".ppt"),
   PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation", ".pptx"),
   RTF("application/rtf", ".rtf"),
   TIF("image/tiff", ".tif"),
   TIFF("image/tiff", ".tiff"),
   TXT("text/plain", ".txt"),
   XLS("application/vnd.ms-excel", ".xls"),
   XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx"),
   XML("application/xml", ".xml");

   private final String contentType;
   private final String fileExtension;

   public static final String OCTET_STREAM = "application/octet-stream";

   public static final Set<String> FILE_EXTENSIONS = Arrays.stream(values())
           .map(ContentType::getFileExtension)
           .collect(Collectors.toSet());

   private static final Map<String, ContentType> MAP;

   static {
      Map<String, ContentType> map = new ConcurrentHashMap<>();
      for (ContentType contentType : ContentType.values()) {
         map.put(contentType.getFileExtension().toLowerCase(), contentType);
      }

      MAP = Collections.unmodifiableMap(map);
   }

   public static Optional<ContentType> getContentTypeByFileExtension(String fileExtension) {
      if ( !MAP.containsKey(fileExtension) ) {
         return Optional.empty();
      }

      return Optional.of(MAP.get(fileExtension.toLowerCase()));
   }

}
