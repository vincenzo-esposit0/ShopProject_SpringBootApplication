package com.example.shop_project.controller;

import com.example.shop_project.model.AuthView;
import com.example.shop_project.service.AuthService;
import com.example.shop_project.util.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthController(AuthService authService, JwtUtils jwtUtils) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthView authView) {
        try {
            // Chiama il metodo di login di AuthService per generare il token
            String token = authService.login(authView);

            // Imposta il token nel cookie
            ResponseCookie jwtCookie = ResponseCookie.from("Test", token)
                    .httpOnly(true)
                    .secure(false) // Imposta a 'true' in produzione per HTTPS
                    .path("/")
                    .maxAge(3600) // Scadenza del cookie in secondi
                    .build();

            // Ritorna il cookie nella risposta
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body("Login successful");

        } catch (AuthException e) {
            // Restituisce una risposta di errore se ci sono problemi con il login
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
        }
    }

    // Endpoint di test per token JWT
    @GetMapping("/protected")
    public ResponseEntity<?> protectedEndpoint(@RequestHeader("Cookie") String authorizationHeader) {
        try {
            // Verifica del token JWT
            if (authorizationHeader == null || !authorizationHeader.startsWith("Test=")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header missing or invalid");
            }

            String token = authorizationHeader.substring(5);
            if (!jwtUtils.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
            }

            // Estrai le informazioni dell'utente dal token
            Claims claims = jwtUtils.getClaimsFromToken(token);
            String username = claims.getSubject();
            Long userId = claims.get("userId", Long.class);
            String role = claims.get("role", String.class);

            // Controllo del ruolo dell'utente
            if ("ADMIN".equalsIgnoreCase(role)) {
                // Accesso consentito per ADMIN
                return ResponseEntity.ok("Accesso consentito per ADMIN. Utente: " + username);
            } else if ("USER".equalsIgnoreCase(role)) {
                // Accesso consentito per USER
                return ResponseEntity.ok("Accesso consentito per USER. Utente: " + username);
            } else {
                // Ruolo non riconosciuto
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato. Ruolo non valido: " + role);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

}
