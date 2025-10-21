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

import home.exercise.java_programming_demo.core.UserManagementService;
import home.exercise.java_programming_demo.db.User;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/users")
@RestControllerAdvice
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")

public class UserController {
    private final UserManagementService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") String id) {
        return userService.getUserById(UUID.fromString(id));
    }

    @PostMapping
    public void addUser(@RequestBody User user) {
        userService.addUser(user);
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable("id") String id) {
        userService.removeUser(UUID.fromString(id));
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable("id") String id, @RequestBody User user) throws Exception{
        User userId = getUserById(id);
        if(userId != null){
            userService.updateUser(user);
        }else{
            throw new Exception("Cannot update user");
        }
    } 
}
