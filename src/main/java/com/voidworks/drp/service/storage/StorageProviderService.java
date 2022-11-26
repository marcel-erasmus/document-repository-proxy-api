package com.voidworks.drp.service.storage;

import com.voidworks.drp.exception.database.DatabasePersistenceException;
import com.voidworks.drp.model.database.storage.StorageProviderDocument;
import com.voidworks.drp.model.mapper.PojoMapper;
import com.voidworks.drp.model.service.StorageProviderBean;
import com.voidworks.drp.repository.storage.StorageProviderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class StorageProviderService {

    private final StorageProviderRepository storageProviderRepository;
    private final PojoMapper pojoMapper;

    @Autowired
    public StorageProviderService(StorageProviderRepository storageProviderRepository,
                                  PojoMapper pojoMapper) {
        this.storageProviderRepository = storageProviderRepository;
        this.pojoMapper = pojoMapper;
    }

    public StorageProviderBean save(StorageProviderBean storageProviderBean) {
        try {
            StorageProviderDocument storageProviderDocument = storageProviderRepository.save(
                    pojoMapper.map(storageProviderBean, new StorageProviderDocument())
            );

            return pojoMapper.map(storageProviderDocument, new StorageProviderBean());
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            throw new DatabasePersistenceException(e.getMessage(), e);
        }
    }

    public void delete(String id) {
        storageProviderRepository.deleteById(id);
    }

    public Optional<StorageProviderBean> findById(String id) {
        Optional<StorageProviderDocument> storageProviderDocument = storageProviderRepository.findById(id);

        if (storageProviderDocument.isEmpty()) {
            return Optional.empty();
        }

        StorageProviderBean resultBean = pojoMapper.map(storageProviderDocument.get(), new StorageProviderBean());

        return Optional.of(resultBean);
    }

}
