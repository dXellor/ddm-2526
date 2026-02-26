package com.ddm.server.bll.contracts;

import com.ddm.server.bll.dtos.upload.SecurityDocumentFileResponse;
import com.ddm.server.bll.dtos.upload.SecurityDocumentInfo;
import com.ddm.server.bll.dtos.upload.SecurityDocumentUploadRequest;

import java.io.IOException;

public interface IDocumentParsingService {
    SecurityDocumentInfo parseAndSave(SecurityDocumentUploadRequest uploadRequest) throws Exception;
    void delete(Long id);
    SecurityDocumentInfo index(SecurityDocumentInfo documentInfo);
    SecurityDocumentFileResponse getFile(Long id) throws Exception;
}
