package com.example.shop_project.service;

import com.example.shop_project.entity.Item;
import com.example.shop_project.entity.Order;
import com.example.shop_project.entity.User;
import com.example.shop_project.model.OrderView;
import com.example.shop_project.repository.ItemRepository;
import com.example.shop_project.repository.OrderRepository;
import com.example.shop_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    public List<OrderView> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> new OrderView(order.getUser().getId(), convertItemsToIds(order.getItems()), order.getTotalAmount()))
                .collect(Collectors.toList());
    }


    public OrderView getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElse(null);

        if(order == null) {
            throw new NoSuchElementException();
        }

        //Gestione Item
        List<Long> itemList = convertItemsToIds(order.getItems());

        OrderView orderView = new OrderView(order.getId(), itemList, order.getTotalAmount());

        return orderView;
    }

    public Order createOrder(OrderView orderView) {
        //Gestione Utente
        Long id = orderView.getIdUser();
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));

        //Gestione Item
        List<Item> itemList = itemRepository.findByIdIn(orderView.getItemIdList());

        //Creazione dell'order
        Order order = new Order(user, itemList, orderView.getTotalAmount());

        System.out.println("Orders: " + orderRepository.findAll());
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public List<OrderView> getOrderByUserId(Long userId) {
        return orderRepository.findByUserID(userId)
                .stream()
                .map(order -> new OrderView(order.getUser().getId(), convertItemsToIds(order.getItems()), order.getTotalAmount()))
                .collect(Collectors.toList());
    }

    //Metodo di conversione da List<Item> a List<Long>
    public List<Long> convertItemsToIds(List<Item> items) {
        return items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
    }

}

