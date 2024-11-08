package com.example.shop_project.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemView {

    private String name;
    private Double price;
    private int quantity;

    public ItemView(String name, Double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}
