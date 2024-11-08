package com.example.shop_project.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {
    // Chiave segreta codificata in Base64
    private final String secretKey = "questaEUnSuperSegretoCheDeveEssereSicuro123456"; // Può essere più lunga

    // Metodo per ottenere la chiave in formato SecretKeySpec
    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    // Metodo per generare il token JWT
    public String generateToken(String username, Long userId, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Metodo per validare il token JWT
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey()) // Chiave per la verifica
                    .build()
                    .parseClaimsJws(token); // Decodifica il token
            return true;
        } catch (SignatureException e) {
            System.out.println("Firma del token non valida: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Errore nella validazione del token: " + e.getMessage());
            return false;
        }
    }

    // Metodo per ottenere i claims dal token JWT (è possibile selezionarli Claims.get())
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
