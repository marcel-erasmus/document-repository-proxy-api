package com.voidworks.drc.service.document;

import java.io.InputStream;

public interface DocumentService {

    InputStream downloadDocument(String key) throws Exception;

}
