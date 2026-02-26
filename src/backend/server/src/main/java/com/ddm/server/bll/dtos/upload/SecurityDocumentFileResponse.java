package com.ddm.server.bll.dtos.upload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SecurityDocumentFileResponse {
    byte[] file;
    String fileName;
}
