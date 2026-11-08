package com.kroon.its.libraryservice.rest;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {

        var jwtAuth = (JwtAuthenticationToken) authentication;

        return Map.of(
                "username", authentication.getName(),
                "email", jwtAuth.getToken().getClaimAsString("email") == null
                        ? ""
                        : jwtAuth.getToken().getClaimAsString("email"),
                "authorities", authentication.getAuthorities(),
                "issuer", jwtAuth.getToken().getIssuer().toString()
        );
    }
}
