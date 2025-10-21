package home.exercise.java_programming_demo.core;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import home.exercise.java_programming_demo.db.User;
import home.exercise.java_programming_demo.db.service.UserServiceRepository;

@Service
@AllArgsConstructor
public class UserManagementService implements UserService {
    private final UserServiceRepository userServiceRepository;

    public void addUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if(userServiceRepository.findById(user.getId()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }
        //encrypt my password with bcrypt
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userServiceRepository.save(user);
    }
    public void removeUser(UUID userId) {
        userServiceRepository.deleteById(userId);
    }
    public void updateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if(userServiceRepository.findById(user.getId()).isPresent()) {
            userServiceRepository.save(user);
        }
    }
    public User getUserById(UUID id) {
        return userServiceRepository.findById(id).orElseGet(() -> null);
    }
    public List<User> getAllUsers() {
        return userServiceRepository.findAll();
    }
}
