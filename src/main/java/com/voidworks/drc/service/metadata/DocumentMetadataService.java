package com.voidworks.drc.service.metadata;

import com.voidworks.drc.exception.DatabasePersistenceException;
import com.voidworks.drc.model.document.DocumentMetadataDocument;
import com.voidworks.drc.model.mapper.PojoMapper;
import com.voidworks.drc.model.service.DocumentMetadataBean;
import com.voidworks.drc.repository.DocumentMetadataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class DocumentMetadataService {

    private final DocumentMetadataRepository documentMetadataRepository;
    private final PojoMapper pojoMapper;

    @Autowired
    public DocumentMetadataService(DocumentMetadataRepository documentMetadataRepository,
                                   PojoMapper pojoMapper) {
        this.documentMetadataRepository = documentMetadataRepository;
        this.pojoMapper = pojoMapper;
    }

    public DocumentMetadataBean save(DocumentMetadataBean documentMetadataBean) {
        try {
            DocumentMetadataDocument documentMetadataDocument = documentMetadataRepository.save(
                    pojoMapper.map(documentMetadataBean, new DocumentMetadataDocument())
            );

            return pojoMapper.map(documentMetadataDocument, new DocumentMetadataBean());
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            throw new DatabasePersistenceException(e.getMessage());
        }
    }

    public void delete(String id) {
        documentMetadataRepository.deleteById(id);
    }

    public Optional<DocumentMetadataBean> findById(String id) {
        Optional<DocumentMetadataDocument> documentMetadataDocument = documentMetadataRepository.findById(id);

        if (documentMetadataDocument.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
                pojoMapper.map(documentMetadataDocument.get(), new DocumentMetadataBean())
        );
    }

}
