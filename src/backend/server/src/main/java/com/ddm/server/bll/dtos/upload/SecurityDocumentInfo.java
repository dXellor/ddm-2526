package com.ddm.server.bll.dtos.upload;

import com.ddm.server.dll.models.SecurityDocument;
import com.ddm.server.dll.models.enums.ThreatLevel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@JsonSerialize
@Data
public class SecurityDocumentInfo {
    private String fullName;

    private String orgName;

    private String orgAddress;

    private String threatName;

    private String threatDescription;

    private ThreatLevel threatLevel;

    private String threatSampleHash;

    private String documentContent;
    public SecurityDocumentInfo(SecurityDocument document){
        this.fullName = document.getFullName();
        this.orgName = document.getOrgName();
        this.orgAddress = document.getOrgAddress();
        this.threatName = document.getThreatName();
        this.threatDescription = document.getThreatDescription();
        this.threatLevel = document.getThreatLevel();
        this.threatSampleHash = document.getThreatSampleHash();
        this.documentContent = document.getDocumentContent();
    }
}
