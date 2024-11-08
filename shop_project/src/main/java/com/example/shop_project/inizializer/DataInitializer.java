package com.example.shop_project.inizializer;

import com.example.shop_project.entity.Item;
import com.example.shop_project.entity.Order;
import com.example.shop_project.entity.User;
import com.example.shop_project.repository.ItemRepository;
import com.example.shop_project.repository.OrderRepository;
import com.example.shop_project.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        // Creazione di alcuni utenti
        User user1 = new User("John", "Doe", "john.doe@example.com", passwordEncoder.encode("password"), "ADMIN");
        User user2 = new User("Jane", "Smith", "jane.smith@example.com", passwordEncoder.encode("password"), "USER");

        userRepository.saveAll(Arrays.asList(user1, user2));

        // Creazione di alcuni items
        Item item1 = new Item("Item 1", 10.99, 10);
        Item item2 = new Item("Item 2", 15.99, 20);

        itemRepository.saveAll(Arrays.asList(item1, item2));

        // Creazione di un ordine per l'utente
        Order order1 = new Order();
        order1.setUser(user1);  // Associa l'ordine all'utente
        order1.addItem(item1);   // Aggiungi l'item all'ordine
        order1.addItem(item2);
        order1.setTotalAmount(23.10);

        orderRepository.save(order1);

        // Stampa dei dati iniziali
        System.out.println("Data initialized:");
        System.out.println("Users: " + userRepository.findAll());
        //userRepository.findAll().forEach(user -> System.out.println(user.toString()));
        System.out.println("Items: " + itemRepository.findAll());
        System.out.println("Orders: " + orderRepository.findAll());
    }
}
