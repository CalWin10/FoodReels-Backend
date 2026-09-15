package com.foodreels.backend.integration;

import com.foodreels.backend.support.BackendIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

class BackendValidationTests extends BackendIntegrationTest {
    @Test
    void adminCreateUser_shouldAcceptValidEnumRole() throws Exception {
        mvc.perform(post("/api/users").with(as(admin)).contentType(MediaType.APPLICATION_JSON).content("""
                {"name":"New user","email":"admin-created@example.test","password":"Test-password-123","role":"USER"}
                """)).andExpect(status().isCreated()).andExpect(jsonPath("$.role").value("USER"));
        var created = users.findByEmail("admin-created@example.test").orElseThrow();
        assertThat(passwordEncoder.matches("Test-password-123", created.getPassword())).isTrue();
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
                {"email":"admin-created@example.test","password":"Test-password-123"}
                """)).andExpect(status().isOk());
    }

    @Test
    void adminUpdateUser_shouldStoreEncodedPasswordAndAllowLogin() throws Exception {
        mvc.perform(put("/api/users/{id}", customer.getId()).with(as(admin))
                        .contentType(MediaType.APPLICATION_JSON).content("""
                {"name":"Updated","email":"%s","password":"New-password-123","role":"USER"}
                """.formatted(customer.getEmail())))
                .andExpect(status().isOk());
        em.flush();
        em.clear();
        assertThat(passwordEncoder.matches("New-password-123", users.findById(customer.getId()).orElseThrow().getPassword()))
                .isTrue();
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
                {"email":"%s","password":"New-password-123"}
                """.formatted(customer.getEmail()))).andExpect(status().isOk());
    }

    @Test
    void adminCreateUser_shouldRejectMissingRole() throws Exception {
        mvc.perform(post("/api/users").with(as(admin)).contentType(MediaType.APPLICATION_JSON).content("""
                {"name":"New user","email":"admin-created@example.test","password":"Test-password-123"}
                """)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors.role").exists());
    }

    @Test
    void login_shouldRejectWrongPassword() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
                {"email":"%s","password":"Wrong-password-123"}
                """.formatted(customer.getEmail())))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.path").value("/api/auth/login"));
    }

    @Test
    void login_shouldRejectUnknownEmail() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
                {"email":"unknown@example.test","password":"Wrong-password-123"}
                """))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void register_shouldRejectDuplicateEmail() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("""
                {"name":"Duplicate","email":"%s","password":"Test-password-123"}
                """.formatted(customer.getEmail())))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void registerAndLogin_shouldRemainPublicAndIssueJwt() throws Exception {
        String email = "registration@example.test";
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("""
                {"name":"New user","email":"%s","password":"Test-password-123"}
                """.formatted(email))).andExpect(status().isOk());
        var saved = users.findByEmail(email).orElseThrow();
        assertThat(saved.getRole().name()).isEqualTo("USER");
        assertThat(saved.getPassword()).isNotEqualTo("Test-password-123");
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
                {"email":"%s","password":"Test-password-123"}
                """.formatted(email))).andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty()).andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void invalidRegistration_shouldReturnValidationEnvelope() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/api/auth/register"))
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    void missingRestaurant_shouldReturnStandardized404() throws Exception {
        mvc.perform(get("/api/restaurants/{id}", Long.MAX_VALUE).with(as(customer)))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/api/restaurants/" + Long.MAX_VALUE));
    }

    @Test
    void missingFood_shouldReturnStandardized404() throws Exception {
        mvc.perform(get("/api/foods/{id}", Long.MAX_VALUE).with(as(customer)))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void missingReel_shouldReturnStandardized404() throws Exception {
        mvc.perform(get("/api/reels/{id}", Long.MAX_VALUE).with(as(customer)))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void missingUser_shouldReturnStandardized404() throws Exception {
        mvc.perform(get("/api/users/{id}", Long.MAX_VALUE).with(as(admin)))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void malformedJson_shouldReturn400() throws Exception {
        mvc.perform(post("/api/orders").with(as(customer)).contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.path").value("/api/orders"));
    }

    @Test
    void invalidPageType_shouldReturn400() throws Exception {
        mvc.perform(get("/api/orders").with(as(customer)).param("page", "invalid"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void invalidOrderStatus_shouldReturn400() throws Exception {
        mvc.perform(get("/api/orders").with(as(customer)).param("status", "UNKNOWN"))
                .andExpect(status().isBadRequest());
    }
}
