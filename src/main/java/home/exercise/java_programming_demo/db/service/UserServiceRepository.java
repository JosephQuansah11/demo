package home.exercise.java_programming_demo.db.service;

// import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import home.exercise.java_programming_demo.db.User;

public interface UserServiceRepository extends JpaRepository<User, UUID> {
    // JpaRepository already provides findAll(), findById(), save(), deleteById()
    // We only need to add custom methods if needed
    // List<User> findByUserName(String userName);
    // @SuppressWarnings("null")
    // void deleteById(UUID id);
    Optional<User> findByEmail(String email);
}
