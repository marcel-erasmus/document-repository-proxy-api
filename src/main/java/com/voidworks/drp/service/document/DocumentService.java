package com.voidworks.drp.service.document;

import com.voidworks.drp.model.document.DocumentIdentity;
import com.voidworks.drp.model.document.DocumentSource;
import com.voidworks.drp.model.service.DocumentPutRequestBean;
import com.voidworks.drp.model.service.StorageProviderBean;

import java.io.InputStream;

public interface DocumentService {

    void uploadDocument(DocumentPutRequestBean documentPutRequestBean) throws Exception;

    void deleteDocument(DocumentIdentity documentIdentity) throws Exception;

    InputStream downloadDocument(DocumentIdentity documentIdentity) throws Exception;

}
