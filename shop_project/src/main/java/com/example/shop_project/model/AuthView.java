package com.example.shop_project.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthView {
    private String email;
    private String password;
    private String role;

    public AuthView() {}

    public AuthView(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public AuthView(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
