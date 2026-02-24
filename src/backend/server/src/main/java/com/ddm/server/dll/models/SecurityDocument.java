package com.ddm.server.dll.models;

import com.ddm.server.dll.models.enums.ThreatLevel;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "security_documents")
public class SecurityDocument {

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "org_name")
    private String orgName;

    @Column(name = "org_address")
    private String orgAddress;

    @Column(name = "threat_name")
    private String threatName;

    @Column(name = "threat_description")
    private String threatDescription;

    @Column(name = "threat_level")
    @Enumerated(EnumType.STRING)
    private ThreatLevel threatLevel;

    @Column(name = "document_key")
    private String documentKey;

    @Column(name = "threat_sample_hash")
    private String threatSampleHash;

    @Transient
    private String documentContent;
}