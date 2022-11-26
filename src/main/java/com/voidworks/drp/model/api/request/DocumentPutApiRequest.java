package com.voidworks.drp.model.api.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@FieldNameConstants
public class DocumentPutApiRequest {

    private MultipartFile file;
    private String filename;
    private String contentType;
    private Map<String, String> references;
    private String storageProviderId;
    private String key;

}
