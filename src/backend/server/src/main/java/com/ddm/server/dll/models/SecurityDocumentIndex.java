package com.ddm.server.dll.models;

import com.ddm.server.dll.models.enums.ThreatLevel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.springframework.data.annotation.Id;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Data
@Document(indexName = "security_documents")
@Setting(settingPath = "index-config.json")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SecurityDocumentIndex {

    @Id
    @Field(type = FieldType.Keyword, name = "id")
    private String id;

    @Field(type = FieldType.Text, name = "full_name", store = true)
    private String fullName;

    @Field(type = FieldType.Text, name = "org_name", store = true)
    private String orgName;

    @Field(type = FieldType.Text, name = "org_address", store = true)
    private String orgAddress;

    @Field(type = FieldType.Text, name = "threat_name", store = true)
    private String threatName;

    @Field(type = FieldType.Text, name = "threat_description", store = true)
    private String threatDescription;

    @Field(type = FieldType.Keyword, name = "threat_level", store = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ThreatLevel threatLevel;

    @Field(type = FieldType.Text, name = "threat_sample_hash", store = true)
    private String threatSampleHash;

    @Field(type = FieldType.Text, analyzer = "serbian", searchAnalyzer = "serbian", name = "document_content")
    private String documentContent;

    @GeoPointField
    @Field(name = "geo_point", store = true)
    private GeoPoint geoPoint;

    @JsonSerialize(using = ToStringSerializer.class)
    @Field(name="vectorized_content", type = FieldType.Dense_Vector, dims = 768, similarity = "cosine")
    private float[] vectorizedContent;

    @Field(type = FieldType.Long, name = "system_id", store = true)
    private Long systemId;
}