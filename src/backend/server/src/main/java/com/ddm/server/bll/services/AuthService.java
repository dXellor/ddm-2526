package com.ddm.server.bll.services;

import com.ddm.server.bll.contracts.IAuthService;
import com.ddm.server.bll.dtos.auth.UserLoginRequest;
import com.ddm.server.bll.dtos.auth.UserLoginResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

        String jwt = jwtService.generateToken(
                (UserDetails) authentication.getPrincipal()
        );
        return new UserLoginResponse(request.getUsername(), jwt);
    }
}
