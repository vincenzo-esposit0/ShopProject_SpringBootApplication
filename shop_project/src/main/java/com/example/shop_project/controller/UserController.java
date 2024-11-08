package com.example.shop_project.controller;

import com.example.shop_project.entity.User;
import com.example.shop_project.model.OrderView;
import com.example.shop_project.model.UserView;
import com.example.shop_project.service.OrderService;
import com.example.shop_project.service.UserService;
import com.example.shop_project.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/all")
    public List<UserView> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/")
    public User createUser(@RequestBody UserView userView) {
        return userService.createUser(userView);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/{id}")
    public UserView findUserById(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    @GetMapping("/orders")
    public ResponseEntity<?> findUserOrders(@CookieValue(value = "Test", required = false) String token) {
        try {
            // Verifica se il token è presente e valido
            if (token == null || !jwtUtils.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing JWT token");
            }

            // Estrai l'utente dal token
            Claims claims = jwtUtils.getClaimsFromToken(token);
            Long userId = claims.get("userId", Long.class);

            // Identifica l'utente e restituisce gli ordini associati al suo account
            List<OrderView> orders = orderService.getOrderByUserId(userId);
            return ResponseEntity.ok(orders);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }


}

