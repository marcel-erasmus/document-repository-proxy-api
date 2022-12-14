package com.voidworks.drp.controller;

import com.voidworks.drp.exception.metadata.DocumentMetadataNotFoundException;
import com.voidworks.drp.model.api.response.ApiResponse;
import com.voidworks.drp.model.service.DocumentMetadataBean;
import com.voidworks.drp.service.metadata.DocumentMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/document-repo")
public class DocumentMetadataController {

    private final DocumentMetadataService documentMetadataService;

    @Autowired
    public DocumentMetadataController(DocumentMetadataService documentMetadataService) {
        this.documentMetadataService = documentMetadataService;
    }

    @PutMapping("/v1.0/metadata")
    public ResponseEntity<ApiResponse<DocumentMetadataBean>> save(@RequestBody DocumentMetadataBean documentMetadataBean) {
        return ResponseEntity.ok(
                getApiResponse(documentMetadataService.save(documentMetadataBean))
        );
    }

    @GetMapping("/v1.0/metadata/{id}")
    public ResponseEntity<ApiResponse<DocumentMetadataBean>> findById(@PathVariable("id") String id) {
        Optional<DocumentMetadataBean> documentMetadataBeanOptional = documentMetadataService.findById(id);

        if (documentMetadataBeanOptional.isEmpty()) {
            throw new DocumentMetadataNotFoundException(id);
        }

        DocumentMetadataBean documentMetadataBean = documentMetadataBeanOptional.get();

        return ResponseEntity.ok(getApiResponse(documentMetadataBean));
    }

    private ApiResponse<DocumentMetadataBean> getApiResponse(DocumentMetadataBean documentMetadataBean) {
        return ApiResponse.<DocumentMetadataBean>builder()
                .data(Collections.singletonList(documentMetadataBean))
                .build();
    }

}
