package com.ddm.server.bll.dtos.search;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BqSearchRequest {
    private String query;
}
