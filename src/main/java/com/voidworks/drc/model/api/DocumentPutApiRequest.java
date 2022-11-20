package com.voidworks.drc.model.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class DocumentPutApiRequest {

    private MultipartFile file;
    private String filename;
    private String contentType;
    private Map<String, String> references;
    private String storageProvider;
    private String key;

}
