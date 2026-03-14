package com.advocacia.gestao.dto;

import java.time.LocalDateTime;

public class UsuarioDTO {

    private Long id;
    private String username;
    private String email;
    private String role;
    private String fullName;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public UsuarioDTO() {
    }

    public UsuarioDTO(Long id, String username, String email, String role, String fullName, Boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.fullName = fullName;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getFullName() {
        return fullName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}