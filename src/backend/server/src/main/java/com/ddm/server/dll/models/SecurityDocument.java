package com.ddm.server.dll.models;

import com.ddm.server.dll.models.enums.ThreatLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Getter
@Setter
@Entity
@Table(name = "security_documents")
@Document( indexName = "security_documents")
public class SecurityDocument {

    @Id
    @Field(name = "id")
    private long id;

    @Column(name = "full_name")
    @Field(type = FieldType.Text, name = "full_name", store = true)
    private String fullName;

    @Column(name = "org_name")
    @Field(type = FieldType.Text, name = "org_name", store = true)
    private String orgName;

    @Column(name = "org_address")
    @Field(type = FieldType.Text, name = "org_address", store = true)
    private String orgAddress;

    @Column(name = "threat_name")
    @Field(type = FieldType.Text, name = "threat_name", store = true)
    private String threatName;

    @Column(name = "threat_level")
    @Enumerated(EnumType.STRING)
    @Field(type = FieldType.Text, name = "threat_level", store = true)
    private ThreatLevel threatLevel;

    @Column(name = "threatSampleHash")
    @Field(type = FieldType.Text, name = "threatSampleHash", store = true)
    private String threatSampleHash;

    @Transient
    @Field(type = FieldType.Text, analyzer = "serbian", searchAnalyzer = "serbian")
    private String documentContent;

    @Column(name = "document_key")
    private String documentKey;

    @Transient
    @GeoPointField
    @Field(name = "geoPoint", store = true)
    private GeoPoint geoPoint;
}
