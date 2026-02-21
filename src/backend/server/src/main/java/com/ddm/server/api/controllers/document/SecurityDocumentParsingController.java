package com.ddm.server.api.controllers.document;

import com.ddm.server.bll.contracts.IDocumentParsingService;
import com.ddm.server.bll.dtos.upload.SecurityDocumentInfo;
import com.ddm.server.bll.dtos.upload.SecurityDocumentUploadRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequestMapping("/document")
@Controller
public class SecurityDocumentParsingController {

    private final IDocumentParsingService documentService;

    public SecurityDocumentParsingController(IDocumentParsingService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(value = "/parse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> parseDocument(/*@AuthenticationPrincipal UserDetails userDetails,*/
                                           @ModelAttribute SecurityDocumentUploadRequest uploadRequest) {
        try {
            SecurityDocumentInfo parsedDocument = this.documentService.parseAndSave(uploadRequest);
            log.info("User {} parsed a file named {}.", "mr.epic", uploadRequest.getDocument().getOriginalFilename());
            return ResponseEntity.ok(parsedDocument);
        } catch (Exception e) {
            log.error("Error while parsing a file named {} for user {}. Error message: {}.", uploadRequest.getDocument().getOriginalFilename(), "mr.epic", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
