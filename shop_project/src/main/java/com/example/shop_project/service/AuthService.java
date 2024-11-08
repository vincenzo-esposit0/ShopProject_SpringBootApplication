package com.example.shop_project.service;

import com.example.shop_project.entity.User;
import com.example.shop_project.model.AuthView;
import com.example.shop_project.repository.UserRepository;
import com.example.shop_project.util.JwtUtils;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public String login(AuthView authView) throws AuthException {
        User userByEmail = userRepository.findByEmail(authView.getEmail());

        if(userByEmail == null){
            throw new AuthException("User is null");
        }

        if(!passwordEncoder.matches(authView.getPassword(), userByEmail.getPassword())){
            throw new AuthException("Password not matched");
        }

        return jwtUtils.generateToken(userByEmail.getEmail(), userByEmail.getId(), userByEmail.getRole());
    }



}

