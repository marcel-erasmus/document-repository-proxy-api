package com.voidworks.drp.controller;

import com.voidworks.drp.enums.storage.StorageProvider;
import com.voidworks.drp.enums.type.ContentType;
import com.voidworks.drp.exception.metadata.DocumentMetadataNotFoundException;
import com.voidworks.drp.exception.storage.StorageProviderConfigurationException;
import com.voidworks.drp.exception.storage.StorageProviderNotSupportedException;
import com.voidworks.drp.model.api.request.DocumentPutApiRequest;
import com.voidworks.drp.model.api.response.ApiResponse;
import com.voidworks.drp.model.document.DocumentIdentity;
import com.voidworks.drp.model.document.DocumentSource;
import com.voidworks.drp.model.mapper.PojoMapper;
import com.voidworks.drp.model.service.DocumentMetadataBean;
import com.voidworks.drp.model.service.DocumentPutRequestBean;
import com.voidworks.drp.model.service.StorageProviderBean;
import com.voidworks.drp.service.document.DocumentService;
import com.voidworks.drp.service.document.FirebaseDocumentService;
import com.voidworks.drp.service.document.S3DocumentService;
import com.voidworks.drp.service.metadata.DocumentMetadataService;
import com.voidworks.drp.service.storage.StorageProviderService;
import com.voidworks.drp.util.FileUtil;
import com.voidworks.drp.util.IdGeneratorUtil;
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

@RestController
@RequestMapping("/document-repo")
public class DocumentController {

    private final ApplicationContext applicationContext;
    private final DocumentMetadataService documentMetadataService;
    private final StorageProviderService storageProviderService;
    private final DocumentPutApiRequestValidator documentPutApiRequestValidator;
    private final PojoMapper pojoMapper;

    @Autowired
    public DocumentController(ApplicationContext applicationContext,
                              DocumentMetadataService documentMetadataService,
                              StorageProviderService storageProviderService,
                              DocumentPutApiRequestValidator documentPutApiRequestValidator,
                              PojoMapper pojoMapper) {
        this.applicationContext = applicationContext;
        this.documentMetadataService = documentMetadataService;
        this.storageProviderService = storageProviderService;
        this.documentPutApiRequestValidator = documentPutApiRequestValidator;
        this.pojoMapper = pojoMapper;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(documentPutApiRequestValidator);
    }

    @PutMapping("/v1.0/documents")
    public ResponseEntity<ApiResponse<DocumentMetadataBean>> uploadDocument(@Validated DocumentPutApiRequest documentPutApiRequest) throws Exception {
        StorageProviderBean storageProviderBean = getStorageProvider(documentPutApiRequest.getStorageProviderId());

        DocumentService documentService = getDocumentService(storageProviderBean.getStorageProvider());

        Optional<ContentType> contentTypeOptional = ContentType.getContentTypeByFileExtension(FileUtil.getFileExtension(documentPutApiRequest.getFilename()));

        String contentType = contentTypeOptional.isPresent() ? contentTypeOptional.get().getContentType() : ContentType.OCTET_STREAM;

        documentPutApiRequest.setKey(
                IdGeneratorUtil.getId() +
                FileUtil.getFileExtension(documentPutApiRequest.getFilename())
        );
        documentPutApiRequest.setContentType(contentType);

        DocumentSource documentSource = new DocumentSource(
                documentPutApiRequest.getStorageProviderId(),
                documentPutApiRequest.getKey()
        );

        DocumentMetadataBean documentMetadataBean = pojoMapper.map(documentPutApiRequest, new DocumentMetadataBean());
        documentMetadataBean.setDocumentSource(documentSource);

        DocumentMetadataBean savedDocumentMetadataBean = documentMetadataService.save(documentMetadataBean);

        DocumentPutRequestBean documentPutRequestBean = pojoMapper.map(documentPutApiRequest, new DocumentPutRequestBean());
        documentPutRequestBean.setFile(documentPutApiRequest.getFile().getInputStream());
        documentPutRequestBean.setStorageProvider(storageProviderBean);
        documentPutRequestBean.setDocumentSource(documentSource);

        documentService.uploadDocument(documentPutRequestBean);

        ApiResponse<DocumentMetadataBean> apiResponse = ApiResponse.<DocumentMetadataBean>builder()
                .data(Collections.singletonList(savedDocumentMetadataBean))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/v1.0/documents/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable("id") String id) throws Exception {
        DocumentMetadataBean documentMetadataBean = getDocumentMetadataBean(id);

        StorageProviderBean storageProviderBean = getStorageProvider(documentMetadataBean.getDocumentSource().storageProviderId());

        DocumentService documentService = getDocumentService(storageProviderBean.getStorageProvider());

        DocumentIdentity documentIdentity = new DocumentIdentity(
                documentMetadataBean.getId(),
                storageProviderBean,
                documentMetadataBean.getDocumentSource()
        );

        documentService.deleteDocument(documentIdentity);

        documentMetadataService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/v1.0/documents/stream/{id}")
    public ResponseEntity<StreamingResponseBody> streamDocument(@PathVariable("id") String id) throws Exception {
        DocumentMetadataBean documentMetadataBean = getDocumentMetadataBean(id);

        StorageProviderBean storageProviderBean = getStorageProvider(documentMetadataBean.getDocumentSource().storageProviderId());

        DocumentService documentService = getDocumentService(storageProviderBean.getStorageProvider());

        DocumentIdentity documentIdentity = new DocumentIdentity(
                documentMetadataBean.getId(),
                storageProviderBean,
                documentMetadataBean.getDocumentSource()
        );

        InputStream inputStream = documentService.downloadDocument(documentIdentity);

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

    private DocumentService getDocumentService(StorageProvider storageProvider) {
        if (storageProvider.equals(StorageProvider.S3)) {
            return applicationContext.getBean(S3DocumentService.class);
        } else if (storageProvider.equals(StorageProvider.FIREBASE)) {
            return applicationContext.getBean(FirebaseDocumentService.class);
        }

        throw new StorageProviderNotSupportedException(storageProvider.name());
    }

    private StorageProviderBean getStorageProvider(String id) {
        Optional<StorageProviderBean> storageProviderBeanOptional = storageProviderService.findById(id);

        if (storageProviderBeanOptional.isEmpty()) {
            throw new StorageProviderConfigurationException(id);
        }

        return storageProviderBeanOptional.get();
    }

    private DocumentMetadataBean getDocumentMetadataBean(String id) {
        Optional<DocumentMetadataBean> documentMetadataBeanOptional = documentMetadataService.findById(id);

        if (documentMetadataBeanOptional.isEmpty()) {
            throw new DocumentMetadataNotFoundException(id);
        }

        return documentMetadataBeanOptional.get();
    }

}
