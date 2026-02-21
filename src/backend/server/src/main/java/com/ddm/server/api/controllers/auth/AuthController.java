package com.ddm.server.api.controllers.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthController {
//    private final AuthorizationService jwtService;
//
//    private final AuthenticationService authenticationService;
//    private static final Logger log = LoggerFactory.getLogger(UserController.class);
//
//    public UserController(AuthorizationService jwtService, AuthenticationService authenticationService) {
//        this.jwtService = jwtService;
//        this.authenticationService = authenticationService;
//    }
//
//
//    @PostMapping("/login")
//    public ResponseEntity<LoginResponseDTO> authenticate(@RequestBody UserLoginDTO loginUserDto) {
//        User authenticatedUser = authenticationService.login(loginUserDto);
//        String jwtToken = jwtService.generateToken(authenticatedUser);
//        LoginResponseDTO loginResponse = new LoginResponseDTO(authenticatedUser.getUsername(), jwtToken);
//        log.info("Successful login for user {}.", authenticatedUser.getUsername());
//        return ResponseEntity.ok(loginResponse);
//    }
}
