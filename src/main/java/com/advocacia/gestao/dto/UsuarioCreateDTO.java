package com.advocacia.gestao.dto;

public class UsuarioCreateDTO {

    private String username;
    private String email;
    private String password;
    private String role;
    private String fullName;

    public UsuarioCreateDTO() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getFullName() {
        return fullName;
    }

}