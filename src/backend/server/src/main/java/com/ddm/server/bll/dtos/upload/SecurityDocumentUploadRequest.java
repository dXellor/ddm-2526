package com.ddm.server.bll.dtos.upload;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SecurityDocumentUploadRequest {
    private MultipartFile document;
}
