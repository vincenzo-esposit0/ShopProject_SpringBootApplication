package com.example.shop_project.exception;

public class UsernameNotFoundException extends Exception {

    private String message;

    public UsernameNotFoundException(String message) {
        super(message);
    }
}

