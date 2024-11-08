package com.example.shop_project.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderView {
    private Long idUser;
    private List<Long> itemIdList = new ArrayList<Long>(); //ArrayList di Id per prendere in input gli item da andare a caricare in DB
    private double totalAmount;

    public OrderView(Long idUser, List<Long> itemIdList, double totalAmount) {
        this.idUser = idUser;
        this.itemIdList = itemIdList;
        this.totalAmount = totalAmount;
    }
}
