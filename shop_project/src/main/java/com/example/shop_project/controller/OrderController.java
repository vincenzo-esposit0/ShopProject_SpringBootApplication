package com.example.shop_project.controller;

import com.example.shop_project.entity.Item;
import com.example.shop_project.entity.Order;
import com.example.shop_project.model.OrderView;
import com.example.shop_project.service.ItemService;
import com.example.shop_project.service.OrderService;
import com.example.shop_project.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/all")
    public ResponseEntity<?> getAllItems(@CookieValue(value = "Test", required = false) String token) {
        try {
            // Verifica se l'utente è ADMIN
            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato: permessi insufficienti");
            }

            // Se l'utente è ADMIN, restituisci tutti gli item
            List<OrderView> order = orderService.getAllOrders();
            return ResponseEntity.ok(order);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id, @CookieValue(value = "Test", required = false) String token) {
        try {
            // Verifica se l'utente è ADMIN
            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato: permessi insufficienti");
            }

            // Se l'utente è ADMIN, restituisci l'ordine per ID
            OrderView order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> createOrder(@CookieValue(value = "Test", required = false) String token, @RequestBody OrderView orderView) {
        try {
            // Verifica se l'utente è ADMIN
            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato: permessi insufficienti");
            }

            if(orderView.getTotalAmount()<=0){
                return ResponseEntity.badRequest().body("L'importo totale deve essere maggiore di zero.");  //Inserire anche un messaggio nella risposta
            }

            // Validazione di altri campi dell'ordine
            if (orderView.getItemIdList() == null || orderView.getItemIdList().isEmpty()) {
                return ResponseEntity.badRequest().body("La lista degli articoli non può essere vuota.");
            }

            // Verifica che i campi obbligatori siano presenti
            if (orderView.getIdUser() == null) {
                return ResponseEntity.badRequest().body("L'ID del cliente è obbligatorio.");
            }

            // Creazione dell'ordine
            Order createdOrder = orderService.createOrder(orderView);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing JWT token");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@CookieValue(value = "Test", required = false) String token, @PathVariable Long id) {
        try {
            // Verifica se l'utente è ADMIN
            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato: permessi insufficienti");
            }

            // Se l'utente è ADMIN, permette l'eliminazione dell'ordine
            orderService.deleteOrder(id);
            return ResponseEntity.ok("Ordine eliminato con successo");

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

