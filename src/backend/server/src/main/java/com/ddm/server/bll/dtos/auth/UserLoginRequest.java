package com.ddm.server.bll.dtos.auth;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String username;
    private String password;
}
