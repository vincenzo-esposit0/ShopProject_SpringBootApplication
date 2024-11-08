package com.example.shop_project.controller;

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
            // Verifica se il token è presente e valido
            if (token == null || !jwtUtils.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing JWT token");
            }

            // Estrai il ruolo dal token
            Claims claims = jwtUtils.getClaimsFromToken(token);
            String role = claims.get("role", String.class);

            // Verifica che l'utente sia un amministratore
            if (!"ADMIN".equals(role)) {
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
    public OrderView getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping("/")
    public ResponseEntity<?> createOrder(@RequestBody OrderView orderView) {
        if(orderView.getTotalAmount()<=0){
            return ResponseEntity.badRequest().body("L'importo totale deve essere maggiore di zero.");  //Inserire anche un messaggio nella risposta
        }

        return ResponseEntity.ok().body(orderService.createOrder(orderView));
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}

