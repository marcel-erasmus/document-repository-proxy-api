package com.voidworks.document.repo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/document-repo")
public class DocumentRepoController {

    @GetMapping("/work-in-progress")
    public ResponseEntity<String> workInProgress() {
        return ResponseEntity.ok("Work in progress...");
    }

}
