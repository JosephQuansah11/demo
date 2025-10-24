package home.exercise.java_programming_demo.db.service;

import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import home.exercise.java_programming_demo.db.User;

public interface UserServiceRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
