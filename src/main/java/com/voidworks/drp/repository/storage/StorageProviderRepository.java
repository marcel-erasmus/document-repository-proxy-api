package com.voidworks.drp.repository.storage;

import com.voidworks.drp.model.database.storage.StorageProviderDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageProviderRepository extends MongoRepository<StorageProviderDocument, String> {

}
