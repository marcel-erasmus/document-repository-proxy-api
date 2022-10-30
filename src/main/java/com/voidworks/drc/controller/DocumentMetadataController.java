package com.voidworks.drc.controller;

import com.voidworks.drc.model.service.DocumentMetadataBean;
import com.voidworks.drc.service.metadata.DocumentMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/document-repo")
public class DocumentMetadataController {

    private DocumentMetadataService documentMetadataService;

    @Autowired
    public DocumentMetadataController(DocumentMetadataService documentMetadataService) {
        this.documentMetadataService = documentMetadataService;
    }

    @PutMapping("/metadata")
    public ResponseEntity<DocumentMetadataBean> create(@RequestBody DocumentMetadataBean documentMetadataBean) {
        return ResponseEntity.ok(documentMetadataService.save(documentMetadataBean));
    }

    @GetMapping("/metadata/{id}")
    public ResponseEntity<DocumentMetadataBean> findById(@PathVariable("id") String id) {
        Optional<DocumentMetadataBean> documentMetadataDocument = documentMetadataService.findById(id);

        if (documentMetadataDocument.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(documentMetadataDocument.get());
    }

}
