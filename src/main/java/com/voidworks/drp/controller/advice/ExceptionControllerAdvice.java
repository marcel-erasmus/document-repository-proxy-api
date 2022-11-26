package com.voidworks.drp.controller.advice;

import com.voidworks.drp.exception.api.MalformedRequestBodyException;
import com.voidworks.drp.exception.metadata.DocumentMetadataNotFoundException;
import com.voidworks.drp.exception.storage.StorageProviderConfigurationException;
import com.voidworks.drp.exception.storage.StorageProviderNotSupportedException;
import com.voidworks.drp.model.api.error.Error;
import com.voidworks.drp.model.api.error.ValidationError;
import com.voidworks.drp.model.api.response.ErrorApiResponse;
import com.voidworks.drp.util.IdGeneratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@ControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size:5MB}")
    private String maxFileSize;

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<ErrorApiResponse> handleException(Exception e) {
        String referenceId = IdGeneratorUtil.getId();

        log.error("Reference ID: " + referenceId + ", Message: " + e.getMessage(), e);

        Error error = new Error(referenceId, "An unknown error has occurred - please enquire with our support team.");

        return getErrorApiResponse(
                Collections.singletonList(error),
                Collections.emptyList(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @Override
    public ResponseEntity<Object> handleBindException(BindException e, HttpHeaders httpHeaders, HttpStatus httpStatus, WebRequest webRequest) {
        List<ObjectError> objectErrors = e.getBindingResult().getAllErrors();

        List<ValidationError> validationErrors = new ArrayList<>();
        objectErrors.forEach(objectError -> {
            if ( !StringUtils.hasText(objectError.getDefaultMessage()) ) {
                validationErrors.add(new ValidationError(objectError.getObjectName(), objectError.getCode()));
            } else {
                validationErrors.add(new ValidationError(objectError.getCode(), objectError.getDefaultMessage()));
            }
        });

        ErrorApiResponse errorApiResponse = ErrorApiResponse.builder()
                .errors(Collections.emptyList())
                .validationErrors(validationErrors)
                .build();

        return new ResponseEntity<>(
                errorApiResponse,
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({ MalformedRequestBodyException.class })
    public ResponseEntity<ErrorApiResponse> handleMalformedRequestBodyException(MalformedRequestBodyException e) {
        Error error = new Error(e.getReferenceId(), e.getMessage());

        return getErrorApiResponse(Collections.singletonList(error));
    }

    @ExceptionHandler({ MaxUploadSizeExceededException.class })
    public ResponseEntity<ErrorApiResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        String referenceId = IdGeneratorUtil.getId();

        log.error("Reference ID: " + referenceId + ", Message: " + e.getMessage(), e);

        Error error = new Error(referenceId, String.format("Maximum file size of [%s] exceeded!", maxFileSize));

        return getErrorApiResponse(Collections.singletonList(error));
    }

    @ExceptionHandler({ StorageProviderConfigurationException.class })
    public ResponseEntity<ErrorApiResponse> handleStorageProviderConfigurationException(StorageProviderConfigurationException e) {
        log.error(e.getMessage(), e);

        Error error = new Error(e.getReferenceId(), String.format("Error with storage provider configuration for [%s]!", e.getStorageProvider()));

        return getErrorApiResponse(Collections.singletonList(error));
    }

    @ExceptionHandler({ StorageProviderNotSupportedException.class })
    public ResponseEntity<ErrorApiResponse> handleStorageProviderNotSupportedException(StorageProviderNotSupportedException e) {
        log.error(e.getMessage(), e);

        Error error = new Error(e.getReferenceId(), e.getMessage());

        return getErrorApiResponse(Collections.singletonList(error));
    }

    @ExceptionHandler({ DocumentMetadataNotFoundException.class })
    public ResponseEntity<ErrorApiResponse> handleDocumentMetadataNotFoundException(DocumentMetadataNotFoundException e) {
        log.error(e.getMessage(), e);

        Error error = new Error(e.getReferenceId(), e.getMessage());

        return getErrorApiResponse(Collections.singletonList(error));
    }

    private ResponseEntity<ErrorApiResponse> getErrorApiResponse(List<Error> errors) {
        return getErrorApiResponse(errors, Collections.emptyList(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorApiResponse> getErrorApiResponse(List<Error> errors, List<ValidationError> validationErrors, HttpStatus httpStatus) {
        ErrorApiResponse errorApiResponse = ErrorApiResponse.builder()
                .errors(errors)
                .validationErrors(validationErrors)
                .build();

        return new ResponseEntity<>(
                errorApiResponse,
                new HttpHeaders(),
                httpStatus
        );
    }
    
}
