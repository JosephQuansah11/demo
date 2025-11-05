package home.exercise.java_programming_demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;

import home.exercise.java_programming_demo.core.UserManagementService;
import home.exercise.java_programming_demo.db.User;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/users")
@RestControllerAdvice
@AllArgsConstructor
@Log4j2
@CrossOrigin(origins = "http://localhost:5173")

public class UserController {
    private final UserManagementService userService;
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/loginPasswordEncoded/{email}/{password}")
    public ResponseEntity<String> loginPasswordEncoded(@PathVariable("email") String email, @PathVariable("password") String password) {
        return ResponseEntity.ok(userService.loginPasswordEncoded(email, password));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") String id) {
        return ResponseEntity.ok(userService.getUserById(UUID.fromString(id)));
    }

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user) {
        // user.setKeycloakId(user.getId().toString());
        user.setId(null);
        log.info("Adding user: {}", user);
        userService.addUser(user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> removeUser(@PathVariable("id") String id) {
        userService.removeUser(UUID.fromString(id));
        return ResponseEntity.ok(userService.getUserById(UUID.fromString(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") String id, @RequestBody User user) throws Exception{
        ResponseEntity<User> userId = getUserById(id);
        if(userId != null){
            userService.updateUser(user);
            return ResponseEntity.ok(user);
        }else{
            throw new Exception("Cannot update user");
        }
    } 
}
