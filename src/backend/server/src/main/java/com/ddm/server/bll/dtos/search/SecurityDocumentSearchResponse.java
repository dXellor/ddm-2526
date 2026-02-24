package com.ddm.server.bll.dtos.search;

import com.ddm.server.dll.models.SecurityDocument;
import com.ddm.server.dll.models.SecurityDocumentIndex;
import com.ddm.server.dll.models.enums.ThreatLevel;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonSerialize
@AllArgsConstructor
public class SecurityDocumentSearchResponse {
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

    public SecurityDocumentSearchResponse(SecurityDocumentIndex documentIndex){
        this.id = documentIndex.getSystemId();
        this.fullName = documentIndex.getFullName();
        this.orgName = documentIndex.getOrgName();
        this.orgAddress = documentIndex.getOrgAddress();
        this.threatName = documentIndex.getThreatName();
        this.threatDescription = documentIndex.getThreatDescription();
        this.threatLevel = documentIndex.getThreatLevel();
        this.threatSampleHash = documentIndex.getThreatSampleHash();
        this.documentContent = documentIndex.getDocumentContent();
    }
}
