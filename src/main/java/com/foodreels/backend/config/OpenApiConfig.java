package com.foodreels.backend.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration

@OpenAPIDefinition(

        info = @Info(

                title = "FoodReels Backend API",

                version = "1.0.0",

                description = """
                        FoodReels backend REST API.

                        Features:
                        - JWT Authentication
                        - Users
                        - Restaurants
                        - Foods
                        - Reels
                        - Likes
                        - Saves
                        - Comments
                        - Watch History
                        - Preferences
                        - Personalized Recommendations
                        - Redis Caching
                        - Search
                        - Location Discovery
                        - Ordering System
                        """,

                contact = @Contact(
                        name = "FoodReels Development Team"
                ),

                license = @License(
                        name = "Internal / Educational Project"
                )
        ),

        servers = {

                @Server(
                        url = "http://localhost:8080",
                        description = "Local Development Server"
                )
        }
)

@SecurityScheme(

        name = "bearerAuth",

        type = SecuritySchemeType.HTTP,

        scheme = "bearer",

        bearerFormat = "JWT",

        description = """
                Enter your JWT Bearer token.

                Example:
                eyJhbGciOiJIUzI1NiJ9...
                """
)

public class OpenApiConfig {
}