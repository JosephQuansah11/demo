package home.exercise.java_programming_demo.core;

import java.util.List;
import java.util.UUID;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.DispatcherSelector;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.CompletionStage;

import home.exercise.java_programming_demo.akka.UserActorProtocol;
import home.exercise.java_programming_demo.db.User;
import home.exercise.java_programming_demo.db.service.UserServiceRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserManagementService implements UserService {
    private final UserServiceRepository userServiceRepository;
    private final ActorSystem<UserActorProtocol> actorSystem;

    // Define the parallelism level for database writes
    private static final int PARALLELISM = 3;

    public void addUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        // encrypt my password with bcrypt
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        if (userServiceRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        // If id is null, persist as a new entity (UUID will be generated)
        if (user.getId() == null) {
            userServiceRepository.save(user); // Hibernate will generate UUID
        }

        // If id is provided, check if it exists and merge
        if (userServiceRepository.existsById(user.getId())) {
            userServiceRepository.save(user); // Merge existing entity
        } else {
            throw new EntityNotFoundException("User with ID " + user.getId() + " does not exist");
        }

        userServiceRepository.save(user);
    }

    public void removeUser(UUID userId) {
        userServiceRepository.deleteById(userId);
    }

    public void updateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (userServiceRepository.findById(user.getId()).isPresent()) {
            userServiceRepository.save(user);
        }
    }

    public User getUserById(UUID id) {
        return userServiceRepository.findById(id).orElseGet(() -> null);
    }

    public List<User> getAllUsers() {
        return userServiceRepository.findAll();
    }

    public String loginPasswordEncoded(String email, String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User foundUser = userServiceRepository.findByEmail(email).orElseGet(() -> null);
        log.info("Login request for user: {} and password {}", email, password);
        boolean isPasswordEncoded = passwordEncoder.matches(password, foundUser.getPassword());
        log.info("User found: {}", foundUser);
        return isPasswordEncoded ? foundUser.getPassword() : null;
    }

    /**
     * Parses the entire file into a list first, then streams the list for parallel
     * DB writes.
     * For truly massive files, consider a streaming Excel parser library.
     */
    // @Transactional
    public CompletionStage<List<User>> importMembers(InputStream fileStream) throws Exception {
        // Step 1: Parse the entire file into a List of ChurchMember objects.
        // This part is still synchronous but is usually very fast.
        List<User> users = parseExcelToList(fileStream);
        if (users.isEmpty()) {
            return CompletableFuture.completedFuture(List.of());
        }

        var blockingIoDispatcher = actorSystem.dispatchers()
                .lookup(DispatcherSelector.fromConfig("akka.actor.blocking-io-dispatcher"));

        // Step 2: Create an Akka Stream to process the list in parallel.
        return Source.from(users)
                .mapAsync(PARALLELISM, user -> {
                    // Run the blocking repository.save() on the dedicated dispatcher
                    return CompletableFuture.supplyAsync(() -> {
                        log.info("Saving user: {}", user.getEmail());
                        userServiceRepository.save(user);
                        return user;
                    }, blockingIoDispatcher);
                })
                // FIX 3: The ActorSystem itself is the Materializer in Akka Typed.
                // Pass it directly to runWith. This fixes the type inference error.
                .runWith(Sink.seq(), actorSystem);
    }

    private List<User> parseExcelToList(InputStream inputStream) throws Exception {
        List<User> users = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header row
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                String fullName = getCellValueAsString(row.getCell(0));
                String email = getCellValueAsString(row.getCell(1));
                String phoneNumber = getCellValueAsString(row.getCell(2));

                if (!fullName.isEmpty() && !email.isEmpty()) {
                    User user = new User();
                    user.setUserName(fullName);
                    user.setEmail(email);
                    user.setTelephone(phoneNumber);
                    users.add(user);
                }
            }
        }
        return users;
    }

    // Helper method to get cell value as string (same as before)
    private String getCellValueAsString(Cell cell) {
        if (cell == null)
            return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}
