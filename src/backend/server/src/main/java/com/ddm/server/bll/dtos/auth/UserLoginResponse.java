package com.ddm.server.bll.dtos.auth;

import lombok.Data;

@Data
public class UserLoginResponse {
    private String username;
    private String token;
}
