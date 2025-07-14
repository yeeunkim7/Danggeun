package org.example.danggeun.user.service;

import lombok.RequiredArgsConstructor;
import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public  class UserService {

    private final UserRepository userRepository;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
