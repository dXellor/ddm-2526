package com.ddm.server.bll.contracts;

import com.ddm.server.bll.dtos.auth.UserLoginRequest;
import com.ddm.server.bll.dtos.auth.UserLoginResponse;

public interface IAuthService {

    UserLoginResponse login(UserLoginRequest request);
}
