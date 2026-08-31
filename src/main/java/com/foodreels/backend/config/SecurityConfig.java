package com.foodreels.backend.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class SecurityConfig {
        @Value("${security.jwt.secret}")
        private String jwtSecret;

        private SecretKey getSecretKey() {

                return new SecretKeySpec(
                                jwtSecret.getBytes(StandardCharsets.UTF_8),
                                "HmacSHA256");
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public JwtEncoder jwtEncoder() {

                return NimbusJwtEncoder
                                .withSecretKey(getSecretKey())
                                .algorithm(MacAlgorithm.HS256)
                                .build();
        }

        @Bean
        public JwtDecoder jwtDecoder() {

                return NimbusJwtDecoder
                                .withSecretKey(getSecretKey())
                                .macAlgorithm(MacAlgorithm.HS256)
                                .build();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http)
                        throws Exception {

                http
                                .csrf(csrf -> csrf.disable())

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/search/**")
                                                .hasAnyRole(
                                                                "USER",
                                                                "RESTAURANT_OWNER",
                                                                "ADMIN")
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/reels/**")
                                                .hasAnyRole(
                                                                "USER",
                                                                "RESTAURANT_OWNER",
                                                                "ADMIN")
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/recommendations/**")
                                                .hasAnyRole(
                                                                "USER",
                                                                "RESTAURANT_OWNER",
                                                                "ADMIN")
                                                .requestMatchers(
                                                                "/api/preferences/**")
                                                .hasAnyRole(
                                                                "USER",
                                                                "RESTAURANT_OWNER",
                                                                "ADMIN")
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/watch-history/**")
                                                .hasAnyRole(
                                                                "USER",
                                                                "RESTAURANT_OWNER",
                                                                "ADMIN")
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/reels/*/view")
                                                .hasAnyRole(
                                                                "USER",
                                                                "RESTAURANT_OWNER",
                                                                "ADMIN")
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/reels/*/saves")
                                                .hasAnyRole(
                                                                "USER",
                                                                "RESTAURANT_OWNER",
                                                                "ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/reels/*/saves")
                                                .hasAnyRole(
                                                                "USER",
                                                                "RESTAURANT_OWNER",
                                                                "ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/saves")
                                                .hasAnyRole(
                                                                "USER",
                                                                "RESTAURANT_OWNER",
                                                                "ADMIN")
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/reels/*/comments")
                                                .hasAnyRole(
                                                                "USER",
                                                                "RESTAURANT_OWNER",
                                                                "ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/comments/*")
                                                .hasAnyRole(
                                                                "USER",
                                                                "RESTAURANT_OWNER",
                                                                "ADMIN")
                                                .requestMatchers("/api/auth/**")
                                                .permitAll()
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/reels/*/likes")
                                                .hasAnyRole(
                                                                "USER",
                                                                "RESTAURANT_OWNER",
                                                                "ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/reels/*/likes")
                                                .hasAnyRole(
                                                                "USER",
                                                                "RESTAURANT_OWNER",
                                                                "ADMIN")

                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/restaurants/**",
                                                                "/api/foods/**",
                                                                "/api/reels/**")
                                                .hasAnyRole(
                                                                "USER",
                                                                "RESTAURANT_OWNER",
                                                                "ADMIN")

                                                .requestMatchers(HttpMethod.POST,
                                                                "/api/restaurants/**",
                                                                "/api/foods/**",
                                                                "/api/reels/**")
                                                .hasAnyRole(
                                                                "RESTAURANT_OWNER",
                                                                "ADMIN")

                                                .requestMatchers(HttpMethod.PUT,
                                                                "/api/restaurants/**",
                                                                "/api/foods/**",
                                                                "/api/reels/**")
                                                .hasAnyRole(
                                                                "RESTAURANT_OWNER",
                                                                "ADMIN")

                                                .requestMatchers(HttpMethod.DELETE,
                                                                "/api/restaurants/**",
                                                                "/api/foods/**",
                                                                "/api/reels/**")
                                                .hasAnyRole(
                                                                "RESTAURANT_OWNER",
                                                                "ADMIN")

                                                .requestMatchers("/api/users/**")
                                                .hasRole("ADMIN")

                                                .anyRequest()
                                                .authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2

                                                .jwt(jwt -> jwt
                                                                .jwtAuthenticationConverter(
                                                                                jwtAuthenticationConverter()))

                                                .authenticationEntryPoint(authenticationEntryPoint())

                                                .accessDeniedHandler(accessDeniedHandler()));

                return http.build();
        }

        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {

                JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();

                authoritiesConverter.setAuthoritiesClaimName("role");
                authoritiesConverter.setAuthorityPrefix("ROLE_");

                JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();

                authenticationConverter.setJwtGrantedAuthoritiesConverter(
                                authoritiesConverter);

                return authenticationConverter;
        }

        @Bean
        public AuthenticationEntryPoint authenticationEntryPoint() {

                return (request, response, authException) -> {

                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");

                        response.getWriter().write(
                                        """
                                                        {
                                                            "status": 401,
                                                            "error": "Unauthorized",
                                                            "message": "Authentication is required"
                                                        }
                                                        """);
                };
        }

        @Bean
        public AccessDeniedHandler accessDeniedHandler() {

                return (request, response, accessDeniedException) -> {

                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json");

                        response.getWriter().write(
                                        """
                                                        {
                                                            "status": 403,
                                                            "error": "Forbidden",
                                                            "message": "You do not have permission to access this resource"
                                                        }
                                                        """);
                };
        }

}