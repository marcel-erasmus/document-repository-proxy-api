package com.voidworks.drc.controller.advice;

import com.voidworks.drc.exception.DocumentMetadataNotFoundException;
import com.voidworks.drc.exception.StorageProviderConfigurationException;
import com.voidworks.drc.exception.StorageProviderNotSupportedException;
import com.voidworks.drc.model.api.response.BaseApiResponse;
import com.voidworks.drc.model.api.error.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@ControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size:5MB}")
    private String maxFileSize;

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<BaseApiResponse> handleException(Exception e) {
        String referenceId = UUID.randomUUID().toString();

        log.error("Reference ID: " + referenceId + "Message: " + e.getMessage(), e);

        Error error = new Error(referenceId, "An unknown error has occurred - please enquire with our support team.");

        return getApiResponse(
                Collections.singletonList(error),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler({ MaxUploadSizeExceededException.class })
    public ResponseEntity<BaseApiResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        String referenceId = UUID.randomUUID().toString();

        log.error("Reference ID: " + referenceId + "Message: " + e.getMessage(), e);

        Error error = new Error(referenceId, String.format("Maximum file size of [%s] exceeded!", maxFileSize));

        return getApiResponse(Collections.singletonList(error));
    }

    @ExceptionHandler({ StorageProviderConfigurationException.class })
    public ResponseEntity<BaseApiResponse> handleStorageProviderConfigurationException(StorageProviderConfigurationException e) {
        log.error(e.getMessage(), e);

        Error error = new Error(e.getReferenceId(), "Error with storage provider configuration!");

        return getApiResponse(Collections.singletonList(error));
    }

    @ExceptionHandler({ StorageProviderNotSupportedException.class })
    public ResponseEntity<BaseApiResponse> handleStorageProviderNotSupportedException(StorageProviderNotSupportedException e) {
        log.error(e.getMessage(), e);

        Error error = new Error(e.getReferenceId(), e.getMessage());

        return getApiResponse(Collections.singletonList(error));
    }

    @ExceptionHandler({ DocumentMetadataNotFoundException.class })
    public ResponseEntity<BaseApiResponse> handleDocumentMetadataNotFoundException(DocumentMetadataNotFoundException e) {
        log.error(e.getMessage(), e);

        Error error = new Error(e.getReferenceId(), e.getMessage());

        return getApiResponse(Collections.singletonList(error));
    }

    private ResponseEntity<BaseApiResponse> getApiResponse(List<Error> errors) {
        return getApiResponse(errors, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<BaseApiResponse> getApiResponse(List<Error> errors, HttpStatus httpStatus) {
        BaseApiResponse baseApiResponse = BaseApiResponse.builder()
                .errors(errors)
                .validationErrors(Collections.emptyList())
                .build();

        return new ResponseEntity<>(
                baseApiResponse,
                new HttpHeaders(),
                httpStatus
        );
    }
    
}
