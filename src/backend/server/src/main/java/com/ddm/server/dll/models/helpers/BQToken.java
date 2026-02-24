package com.ddm.server.dll.models.helpers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BQToken {
    private String fieldName;
    private String fieldValue;
    private BQTokenType type;
}
