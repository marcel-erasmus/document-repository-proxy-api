package com.voidworks.drc.controller;

import com.voidworks.drc.enums.storage.StorageProvider;
import com.voidworks.drc.exception.DocumentMetadataNotFoundException;
import com.voidworks.drc.exception.StorageProviderNotSupportedException;
import com.voidworks.drc.model.api.request.DocumentPutApiRequest;
import com.voidworks.drc.model.api.response.ApiResponse;
import com.voidworks.drc.model.mapper.PojoMapper;
import com.voidworks.drc.model.service.DocumentMetadataBean;
import com.voidworks.drc.model.service.DocumentPutRequestBean;
import com.voidworks.drc.service.document.DocumentService;
import com.voidworks.drc.service.document.FirebaseDocumentService;
import com.voidworks.drc.service.document.S3DocumentService;
import com.voidworks.drc.service.metadata.DocumentMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.util.FileCopyUtils;
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
    private final PojoMapper pojoMapper;

    @Autowired
    public DocumentController(ApplicationContext applicationContext,
                              DocumentMetadataService documentMetadataService,
                              PojoMapper pojoMapper) {
        this.applicationContext = applicationContext;
        this.documentMetadataService = documentMetadataService;
        this.pojoMapper = pojoMapper;
    }

    @PutMapping("/documents")
    public ResponseEntity<ApiResponse<DocumentMetadataBean>> uploadDocument(DocumentPutApiRequest documentPutApiRequest) throws Exception {
        DocumentService documentService = getDocumentService(documentPutApiRequest.getStorageProvider());

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

    @DeleteMapping("/documents/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable("id") String id) throws Exception {
        DocumentMetadataBean documentMetadataBean = getDocumentMetadata(id);

        DocumentService documentService = getDocumentService(documentMetadataBean.getStorageProvider());

        documentService.deleteDocument(documentMetadataBean.getKey());

        documentMetadataService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/documents/stream/{id}")
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
