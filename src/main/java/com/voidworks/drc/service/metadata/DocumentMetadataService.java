package com.voidworks.drc.service.metadata;

import com.voidworks.drc.exception.database.DatabasePersistenceException;
import com.voidworks.drc.model.database.DocumentMetadataDocument;
import com.voidworks.drc.model.mapper.PojoMapper;
import com.voidworks.drc.model.service.DocumentMetadataBean;
import com.voidworks.drc.repository.DocumentMetadataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
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

            DocumentMetadataBean resultBean = pojoMapper.map(documentMetadataDocument, new DocumentMetadataBean());
            if (CollectionUtils.isEmpty(resultBean.getReferences())) {
                resultBean.setReferences(new HashMap<>());
            }

            return resultBean;
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            throw new DatabasePersistenceException(e.getMessage(), e);
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

        DocumentMetadataBean resultBean = pojoMapper.map(documentMetadataDocument.get(), new DocumentMetadataBean());
        if (CollectionUtils.isEmpty(resultBean.getReferences())) {
            resultBean.setReferences(new HashMap<>());
        }

        return Optional.of(resultBean);
    }

}
