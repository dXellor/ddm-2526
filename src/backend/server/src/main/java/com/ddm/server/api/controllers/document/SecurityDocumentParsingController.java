package com.ddm.server.api.controllers.document;

import com.ddm.server.bll.contracts.IDocumentParsingService;
import com.ddm.server.bll.dtos.upload.SecurityDocumentInfo;
import com.ddm.server.bll.dtos.upload.SecurityDocumentUploadRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/document")
@Controller
public class SecurityDocumentParsingController {

    private final IDocumentParsingService documentService;

    public SecurityDocumentParsingController(IDocumentParsingService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(value = "/parse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> parseDocument(@AuthenticationPrincipal UserDetails userDetails,
                                           @ModelAttribute SecurityDocumentUploadRequest uploadRequest) {
        try {
            SecurityDocumentInfo parsedDocument = this.documentService.parseAndSave(uploadRequest);
            log.info("User {} parsed a file named {}.", userDetails.getUsername(), uploadRequest.getDocument().getOriginalFilename());
            return ResponseEntity.ok(parsedDocument);
        } catch (Exception e) {
            log.error("Error while parsing a file named {} for user {}. Error message: {}.", uploadRequest.getDocument().getOriginalFilename(), userDetails.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/index")
    public ResponseEntity<?> indexDocument(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SecurityDocumentInfo documentInfo) {
        try {
            SecurityDocumentInfo parsedDocument = this.documentService.index(documentInfo);
            log.info("User {} indexed a document", userDetails.getUsername());
            return ResponseEntity.ok(parsedDocument);
        } catch (Exception e) {
            log.error("Error while indexing document {} for user {}. Error message: {}.", documentInfo.hashCode(), userDetails.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteDocument(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id){
        this.documentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
