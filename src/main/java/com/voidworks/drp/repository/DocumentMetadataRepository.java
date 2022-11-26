package com.voidworks.drp.repository;

import com.voidworks.drp.model.database.DocumentMetadataDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentMetadataRepository extends MongoRepository<DocumentMetadataDocument, String> {

}
