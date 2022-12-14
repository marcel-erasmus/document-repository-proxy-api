package com.voidworks.drp.model.database.metadata;

import com.voidworks.drp.model.document.DocumentSource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@Document("metadata")
public class DocumentMetadataDocument {

    @Id
    private String id;
    private String filename;
    private String contentType;
    private Map<String, String> references;
    private DocumentSource documentSource;

}
