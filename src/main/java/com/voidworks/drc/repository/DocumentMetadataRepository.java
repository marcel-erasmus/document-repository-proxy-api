package com.voidworks.drc.repository;

import com.voidworks.drc.model.document.DocumentMetadataDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentMetadataRepository extends MongoRepository<DocumentMetadataDocument, String> {

}
