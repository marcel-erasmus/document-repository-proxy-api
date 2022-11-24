package com.voidworks.drc.controller;

import com.voidworks.drc.exception.DocumentMetadataNotFoundException;
import com.voidworks.drc.model.api.response.ApiResponse;
import com.voidworks.drc.model.service.DocumentMetadataBean;
import com.voidworks.drc.service.metadata.DocumentMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/document-repo")
public class DocumentMetadataController {

    private final DocumentMetadataService documentMetadataService;

    @Autowired
    public DocumentMetadataController(DocumentMetadataService documentMetadataService) {
        this.documentMetadataService = documentMetadataService;
    }

    @PutMapping("/metadata")
    public ResponseEntity<ApiResponse<DocumentMetadataBean>> save(@RequestBody DocumentMetadataBean documentMetadataBean) {
        return ResponseEntity.ok(
                getApiResponse(documentMetadataService.save(documentMetadataBean))
        );
    }

    @GetMapping("/metadata/{id}")
    public ResponseEntity<ApiResponse<DocumentMetadataBean>> findById(@PathVariable("id") String id) {
        Optional<DocumentMetadataBean> documentMetadataBeanOptional = documentMetadataService.findById(id);

        if (documentMetadataBeanOptional.isEmpty()) {
            throw new DocumentMetadataNotFoundException(id);
        }

        DocumentMetadataBean documentMetadataBean = documentMetadataBeanOptional.get();

        if (CollectionUtils.isEmpty(documentMetadataBean.getReferences())) {
            documentMetadataBean.setReferences(new HashMap<>());
        }

        return ResponseEntity.ok(getApiResponse(documentMetadataBean));
    }

    private ApiResponse<DocumentMetadataBean> getApiResponse(DocumentMetadataBean documentMetadataBean) {
        return ApiResponse.<DocumentMetadataBean>builder()
                .data(Collections.singletonList(documentMetadataBean))
                .build();
    }

}
