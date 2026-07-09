package com.cognizant.authentication_service.controller;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import java.util.Date;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AuthenticationController.class);

    @GetMapping("/authenticate")
    public Map<String, String> authenticate(
            @RequestHeader("Authorization") String authHeader) {

        LOGGER.info("START");

        LOGGER.debug("Authorization Header : {}", authHeader);

        String user = getUser(authHeader);

        LOGGER.debug("Authenticated User : {}", user);

        String token = generateJwt(user);

        Map<String, String> map = new HashMap<>();

        map.put("token", token);

        LOGGER.info("END");

        return map;
    }

    private String getUser(String authHeader) {

        LOGGER.info("START");

        // Remove "Basic "
        String encodedCredentials = authHeader.substring(6);

        LOGGER.debug("Encoded Credentials : {}", encodedCredentials);

        // Decode Base64
        byte[] decodedBytes = Base64.getDecoder().decode(encodedCredentials);

        String credentials = new String(decodedBytes);

        LOGGER.debug("Decoded Credentials : {}", credentials);

        // Extract username
        String user = credentials.substring(0, credentials.indexOf(":"));

        LOGGER.debug("User : {}", user);

        LOGGER.info("END");

        return user;
    }

    private String generateJwt(String user) {

        LOGGER.info("START");

        JwtBuilder builder = Jwts.builder();

        builder.setSubject(user);

        // Issue time
        builder.setIssuedAt(new Date());

        // Expiry time (20 minutes)
        builder.setExpiration(new Date(new Date().getTime() + 1200000));

        builder.signWith(SignatureAlgorithm.HS256, "secretkey");

        String token = builder.compact();

        LOGGER.debug("Generated Token : {}", token);

        LOGGER.info("END");

        return token;
    }
}