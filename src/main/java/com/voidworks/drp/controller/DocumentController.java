package com.voidworks.drp.controller;

import com.voidworks.drp.enums.storage.StorageProvider;
import com.voidworks.drp.enums.type.ContentType;
import com.voidworks.drp.exception.metadata.DocumentMetadataNotFoundException;
import com.voidworks.drp.exception.storage.StorageProviderNotSupportedException;
import com.voidworks.drp.model.api.request.DocumentPutApiRequest;
import com.voidworks.drp.model.api.response.ApiResponse;
import com.voidworks.drp.model.mapper.PojoMapper;
import com.voidworks.drp.model.service.DocumentMetadataBean;
import com.voidworks.drp.model.service.DocumentPutRequestBean;
import com.voidworks.drp.service.document.DocumentService;
import com.voidworks.drp.service.document.FirebaseDocumentService;
import com.voidworks.drp.service.document.S3DocumentService;
import com.voidworks.drp.service.metadata.DocumentMetadataService;
import com.voidworks.drp.util.FileUtil;
import com.voidworks.drp.validation.DocumentPutApiRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/document-repo")
public class DocumentController {

    private final ApplicationContext applicationContext;
    private final DocumentMetadataService documentMetadataService;
    private final DocumentPutApiRequestValidator documentPutApiRequestValidator;
    private final PojoMapper pojoMapper;

    @Autowired
    public DocumentController(ApplicationContext applicationContext,
                              DocumentMetadataService documentMetadataService,
                              DocumentPutApiRequestValidator documentPutApiRequestValidator,
                              PojoMapper pojoMapper) {
        this.applicationContext = applicationContext;
        this.documentMetadataService = documentMetadataService;
        this.documentPutApiRequestValidator = documentPutApiRequestValidator;
        this.pojoMapper = pojoMapper;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(documentPutApiRequestValidator);
    }

    @PutMapping("/v1.0/documents")
    public ResponseEntity<ApiResponse<DocumentMetadataBean>> uploadDocument(@Validated DocumentPutApiRequest documentPutApiRequest) throws Exception {
        DocumentService documentService = getDocumentService(documentPutApiRequest.getStorageProvider());

        Optional<ContentType> contentTypeOptional = ContentType.getContentTypeByFileExtension(FileUtil.getFileExtension(documentPutApiRequest.getFilename()));

        String contentType = contentTypeOptional.isPresent() ? contentTypeOptional.get().getContentType() : ContentType.OCTET_STREAM;

        documentPutApiRequest.setKey(
                UUID.randomUUID().toString().replaceAll("-", "") +
                FileUtil.getFileExtension(documentPutApiRequest.getFilename())
        );
        documentPutApiRequest.setContentType(contentType);

        DocumentMetadataBean documentMetadataBean = pojoMapper.map(documentPutApiRequest, new DocumentMetadataBean());

        DocumentMetadataBean savedDocumentMetadataBean = documentMetadataService.save(documentMetadataBean);

        DocumentPutRequestBean documentPutRequestBean = pojoMapper.map(documentPutApiRequest, new DocumentPutRequestBean());
        documentPutRequestBean.setFile(documentPutApiRequest.getFile().getInputStream());

        documentService.uploadDocument(documentPutRequestBean);

        ApiResponse<DocumentMetadataBean> apiResponse = ApiResponse.<DocumentMetadataBean>builder()
                .data(Collections.singletonList(savedDocumentMetadataBean))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/v1.0/documents/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable("id") String id) throws Exception {
        DocumentMetadataBean documentMetadataBean = getDocumentMetadata(id);

        DocumentService documentService = getDocumentService(documentMetadataBean.getStorageProvider());

        documentService.deleteDocument(documentMetadataBean.getKey());

        documentMetadataService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/v1.0/documents/stream/{id}")
    public ResponseEntity<StreamingResponseBody> streamDocument(@PathVariable("id") String id) throws Exception {
        DocumentMetadataBean documentMetadataBean = getDocumentMetadata(id);

        DocumentService documentService = getDocumentService(documentMetadataBean.getStorageProvider());

        InputStream inputStream = documentService.downloadDocument(documentMetadataBean.getKey());

        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

        MediaType mediaType = getContentType(documentMetadataBean.getContentType());

        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(documentMetadataBean.getFilename())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(mediaType)
                .body(outputStream -> FileCopyUtils.copy(inputStreamResource.getInputStream(), outputStream));
    }

    private MediaType getContentType(String contentType) {
        try {
            return MediaType.parseMediaType(contentType);
        } catch (InvalidMediaTypeException e){
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    private DocumentService getDocumentService(String storageProvider) {
        if (storageProvider.equals(StorageProvider.S3.name())) {
            return applicationContext.getBean(S3DocumentService.class);
        } else if (storageProvider.equals(StorageProvider.FIREBASE.name())) {
            return applicationContext.getBean(FirebaseDocumentService.class);
        }

        throw new StorageProviderNotSupportedException(storageProvider);
    }

    private DocumentMetadataBean getDocumentMetadata(String id) {
        Optional<DocumentMetadataBean> documentMetadataBeanOptional = documentMetadataService.findById(id);

        if (documentMetadataBeanOptional.isEmpty()) {
            throw new DocumentMetadataNotFoundException(id);
        }

        return documentMetadataBeanOptional.get();
    }

}
