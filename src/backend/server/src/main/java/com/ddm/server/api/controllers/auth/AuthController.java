package com.ddm.server.api.controllers.auth;

import com.ddm.server.bll.contracts.IAuthService;
import com.ddm.server.bll.dtos.auth.UserLoginRequest;
import com.ddm.server.bll.dtos.auth.UserLoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/auth")
@RestController
public class AuthController {
    private final IAuthService authService;
    public AuthController(IAuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        try{
            UserLoginResponse response = this.authService.login(request);
            log.info("Successful login for user {}", response.getUsername());
            return ResponseEntity.ok(response);
        }catch (Exception e){
            log.error("Error on login for user: {}: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
