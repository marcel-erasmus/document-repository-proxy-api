package com.voidworks.drc.controller;

import com.voidworks.drc.enums.storage.StorageProvider;
import com.voidworks.drc.model.service.DocumentMetadataBean;
import com.voidworks.drc.service.document.DocumentService;
import com.voidworks.drc.service.document.FirebaseDocumentService;
import com.voidworks.drc.service.document.S3DocumentService;
import com.voidworks.drc.service.metadata.DocumentMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.util.Optional;

@RestController
@RequestMapping("/document-repo")
public class DocumentController {

    private final ApplicationContext applicationContext;
    private final DocumentMetadataService documentMetadataService;

    @Autowired
    public DocumentController(ApplicationContext applicationContext,
                              DocumentMetadataService documentMetadataService) {
        this.applicationContext = applicationContext;
        this.documentMetadataService = documentMetadataService;
    }

    @GetMapping("/documents/stream/{id}")
    public ResponseEntity<StreamingResponseBody> streamDocument(@PathVariable("id") String id) throws Exception {
        Optional<DocumentMetadataBean> documentMetadataBeanOptional = documentMetadataService.findById(id);

        if (documentMetadataBeanOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DocumentMetadataBean documentMetadataBean = documentMetadataBeanOptional.get();

        DocumentService documentService = getDocumentService(documentMetadataBean.getStorageProvider());

        if (documentService == null) {
            return ResponseEntity.badRequest().build();
        }

        InputStream inputStream = documentService.downloadDocument(documentMetadataBean.getKey());

        MediaType mediaType = getContentType(documentMetadataBean.getContentType());

        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(documentMetadataBean.getFilename())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(mediaType)
                .body(outputStream -> FileCopyUtils.copy(inputStream, outputStream));
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

        return null;
    }

}
