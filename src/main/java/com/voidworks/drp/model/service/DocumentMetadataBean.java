package com.voidworks.drp.model.service;

import com.voidworks.drp.model.document.DocumentSource;
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
    private DocumentSource documentSource;

}
