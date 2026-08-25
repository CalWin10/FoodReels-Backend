package com.foodreels.backend.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.foodreels.backend.user.User;

@Service
public class JwtService {
    private final JwtEncoder jwtEncoder;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateToken(User user) {

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()

                // who created the token
                .issuer("foodreels-backend")

                // who the token belongs to
                .subject(user.getEmail())

                // when token was created
                .issuedAt(now)

                // token expires after 1 hour
                .expiresAt(now.plus(1, ChronoUnit.HOURS))

                // custom claim
                .claim("role", user.getRole().name())

                .build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }
}


