package com.voidworks.drc.controller.advice;

import com.voidworks.drc.exception.StorageProviderConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size:5MB}")
    private String maxFileSize;

    @ExceptionHandler({ MaxUploadSizeExceededException.class })
    public ResponseEntity<Object> handleMaxUploadSizeExceededException(Exception e) {
        log.error(e.getMessage(), e);

        return new ResponseEntity<>(
                String.format("Maximum file size of [%s] exceeded!", maxFileSize), new HttpHeaders(), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({ StorageProviderConfigurationException.class })
    public ResponseEntity<Object> handleStorageProviderConfigurationException(StorageProviderConfigurationException e) {
        log.error(e.getMessage(), e);

        return new ResponseEntity<>(
                String.format("Error with storage provider configuration! Reference ID: %s", e.getReferenceId()), new HttpHeaders(), HttpStatus.BAD_REQUEST
        );
    }
    
}
