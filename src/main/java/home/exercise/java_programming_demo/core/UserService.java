package home.exercise.java_programming_demo.core;

import java.util.List;
import java.util.UUID;

import home.exercise.java_programming_demo.db.User;

public interface UserService {
    void addUser(User user);

    void removeUser(UUID userId);

    void updateUser(User user);

    User getUserById(UUID id);

    List<User> getAllUsers();
}
