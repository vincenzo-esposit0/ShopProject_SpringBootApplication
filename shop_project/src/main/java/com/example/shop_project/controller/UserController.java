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
    public ResponseEntity<?> getAllUsers(@CookieValue(value = "Test", required = false) String token) {
        try {
            // Verifica se l'utente è ADMIN
            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato: permessi insufficienti");
            }

            // Se l'utente è ADMIN, restituisci tutti gli utenti
            List<UserView> users = userService.getAllUsers();
            return ResponseEntity.ok(users);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> createUser(@RequestBody UserView userView, @CookieValue(value = "Test", required = false) String token) {
        try {
            // Verifica se l'utente è ADMIN
            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato: permessi insufficienti");
            }

            // Se l'utente è ADMIN, crea un nuovo utente
            User user = userService.createUser(userView);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findUserById(@PathVariable Long id, @CookieValue(value = "Test", required = false) String token) {
        try {
            // Verifica se l'utente è ADMIN
            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato: permessi insufficienti");
            }

            // Se l'utente è ADMIN, restituisci l'utente per ID
            UserView user = userService.findUserById(id);
            return ResponseEntity.ok(user);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
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

    private boolean isAdmin(String token) {
        // Verifica se il token è valido
        if (token == null || !jwtUtils.validateToken(token)) {
            return false;
        }

        // Estrai il ruolo dal token
        Claims claims = jwtUtils.getClaimsFromToken(token);
        String role = claims.get("role", String.class);

        // Verifica se il ruolo è ADMIN
        return "ADMIN".equals(role);
    }

}

