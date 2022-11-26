package com.voidworks.drp.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.InputStream;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class DocumentPutRequestBean {

    private InputStream file;
    private String filename;
    private String contentType;
    private Map<String, String> references;
    private String storageProvider;
    private String key;

}
