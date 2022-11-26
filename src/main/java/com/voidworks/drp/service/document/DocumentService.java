package com.voidworks.drp.service.document;

import com.voidworks.drp.model.service.DocumentPutRequestBean;

import java.io.InputStream;

public interface DocumentService {

    void uploadDocument(DocumentPutRequestBean documentPutRequestBean) throws Exception;

    void deleteDocument(String key) throws Exception;

    InputStream downloadDocument(String key) throws Exception;

}