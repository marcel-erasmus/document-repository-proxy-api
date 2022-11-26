package com.voidworks.drc.validation;

import com.voidworks.drc.enums.error.ValidationError;
import com.voidworks.drc.enums.type.ContentType;
import com.voidworks.drc.exception.api.MalformedRequestBodyException;
import com.voidworks.drc.model.api.request.DocumentPutApiRequest;
import com.voidworks.drc.util.FileUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class DocumentPutApiRequestValidator implements Validator {

   @Override
   public boolean supports(Class<?> clazz) {
      return clazz.isAssignableFrom(DocumentPutApiRequest.class);
   }

   @Override
   public void validate(Object object, Errors errors) {
      if ( !(object instanceof DocumentPutApiRequest documentPutApiRequest) ) {
         throw new MalformedRequestBodyException();
      }

      if (documentPutApiRequest.getFile() == null) {
         errors.reject(DocumentPutApiRequest.Fields.file, ValidationError.REQUIRED.getMessage());

         return;
      }

      if ( !StringUtils.hasText(documentPutApiRequest.getFilename()) ) {
         errors.reject(DocumentPutApiRequest.Fields.filename, ValidationError.REQUIRED.getMessage());

         return;
      }

      String fileExtension = FileUtil.getFileExtension(documentPutApiRequest.getFilename());

      if ( !FileUtil.getFileExtension(documentPutApiRequest.getFile().getOriginalFilename()).equals(fileExtension) ) {
         errors.reject(DocumentPutApiRequest.Fields.filename, "Actual file extension and file extension indicated in filename must match.");
      }

      if ( !ContentType.FILE_EXTENSIONS.contains(fileExtension) ) {
         errors.reject(DocumentPutApiRequest.Fields.filename, "File extension not supported! Supported file extensions: " + ContentType.FILE_EXTENSIONS);
      }
   }

}
