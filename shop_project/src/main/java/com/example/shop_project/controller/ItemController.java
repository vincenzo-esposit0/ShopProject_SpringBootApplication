package com.example.shop_project.controller;

import com.example.shop_project.entity.Item;
import com.example.shop_project.model.ItemView;
import com.example.shop_project.service.ItemService;
import com.example.shop_project.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final JwtUtils jwtUtils;

    @Autowired
    public ItemController(ItemService itemService, JwtUtils jwtUtils) {
        this.itemService = itemService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllItems(@CookieValue(value = "Test", required = false) String token) {
        try {
            // Verifica se l'utente è ADMIN
            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato: permessi insufficienti");
            }

            // Se l'utente è ADMIN, restituisci tutti gli item
            List<ItemView> items = itemService.getAllItems();
            return ResponseEntity.ok(items);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(@CookieValue(value = "Test", required = false) String token, @PathVariable Long id) {
        try {
            // Verifica se l'utente è ADMIN
            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato: permessi insufficienti");
            }

            // Se l'utente è ADMIN, permette la visualizzazione dell'item
            ItemView item = itemService.getItemById(id);
            return ResponseEntity.ok(item);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing JWT token");
        }
    }

    @PostMapping
    public ResponseEntity<?> createItem(@CookieValue(value = "Test", required = false) String token, @RequestBody ItemView itemView) {
        try {
            // Verifica se l'utente è ADMIN
            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato: permessi insufficienti");
            }

            // Se l'utente è ADMIN, permette la creazione dell'item
            Item createdItem = itemService.createItem(itemView);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing JWT token");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@CookieValue(value = "Test", required = false) String token, @PathVariable Long id) {
        try {
            // Verifica se l'utente è ADMIN
            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato: permessi insufficienti");
            }

            // Se l'utente è ADMIN, permette l'eliminazione dell'item
            itemService.deleteItem(id);
            return ResponseEntity.ok("Item eliminato con successo");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing JWT token");
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

