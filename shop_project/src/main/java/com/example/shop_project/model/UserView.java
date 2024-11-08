package com.example.shop_project.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserView {

    private String firstName;
    private String lastName;
    private String email;

    public UserView(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
