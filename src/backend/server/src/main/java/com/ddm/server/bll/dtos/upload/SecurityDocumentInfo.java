package com.ddm.server.bll.dtos.upload;

import com.ddm.server.dll.models.SecurityDocument;
import com.ddm.server.dll.models.enums.ThreatLevel;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@JsonSerialize
@Data
public class SecurityDocumentInfo {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("orgName")
    private String orgName;

    @JsonProperty("orgAddress")
    private String orgAddress;

    @JsonProperty("threatName")
    private String threatName;

    @JsonProperty("threatDescription")
    private String threatDescription;

    @JsonProperty("threatLevel")
    private ThreatLevel threatLevel;

    @JsonProperty("threatSampleHash")
    private String threatSampleHash;

    @JsonProperty("documentContent")
    private String documentContent;

    public SecurityDocumentInfo(){}

    public SecurityDocumentInfo(Long id, String fullName, String orgName, String orgAddress, String threatName, String threatDescription, String threatSampleHash, ThreatLevel threatLevel, String documentContent) {
        this.id = id;
        this.fullName = fullName;
        this.orgName = orgName;
        this.orgAddress = orgAddress;
        this.threatName = threatName;
        this.threatDescription = threatDescription;
        this.threatSampleHash = threatSampleHash;
        this.threatLevel = threatLevel;
        this.documentContent = documentContent;
    }

    public SecurityDocumentInfo(SecurityDocument document){
        this.id = document.getId();
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
