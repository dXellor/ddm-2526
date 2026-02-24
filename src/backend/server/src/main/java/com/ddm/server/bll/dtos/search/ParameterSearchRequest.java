package com.ddm.server.bll.dtos.search;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonSerialize
public class ParameterSearchRequest {
    private String fieldName;
    private String value;
}
