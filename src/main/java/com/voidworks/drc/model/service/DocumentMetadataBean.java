package com.voidworks.drc.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class DocumentMetadataBean {

    private String id;
    private String filename;
    private String contentType;
    private Map<String, String> references;
    private String storageProvider;
    private String key;

}
