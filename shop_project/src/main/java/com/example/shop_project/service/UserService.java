package com.example.shop_project.service;

import com.example.shop_project.entity.User;
import com.example.shop_project.model.UserView;
import com.example.shop_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserView> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserView(user.getFirstName(), user.getLastName(), user.getEmail()))
                .collect(Collectors.toList());
    }

    public User createUser(UserView userView) {
        User user = new User(userView.getFirstName(), userView.getLastName(), userView.getEmail());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserView findUserById(Long id) {
        //Effettuo un map partendo dallo user
        return userRepository.findById(id).map(user -> new UserView(user.getFirstName(), user.getLastName(), user.getEmail())).orElse(null);
    }
}
