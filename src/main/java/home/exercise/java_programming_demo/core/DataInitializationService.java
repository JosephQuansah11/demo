package home.exercise.java_programming_demo.core;

import com.github.javafaker.Faker;
import home.exercise.java_programming_demo.db.Address;
import home.exercise.java_programming_demo.db.Church;
import home.exercise.java_programming_demo.db.Department;
import home.exercise.java_programming_demo.db.User;
import home.exercise.java_programming_demo.db.UserPreferences;
import home.exercise.java_programming_demo.db.UserProfile;
import home.exercise.java_programming_demo.db.UserRole;
import home.exercise.java_programming_demo.db.service.ChurchRepository;
import home.exercise.java_programming_demo.db.service.DepartmentRepository;
import home.exercise.java_programming_demo.db.service.UserServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService implements CommandLineRunner {

    private final UserServiceRepository userRepository;
    private final ChurchRepository churchRepository;
    private final DepartmentRepository departmentRepository;
    private final DataInitializationProgressService progressService;
    private final FrontendLauncherService frontendLauncherService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private final Faker faker = new Faker();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting threaded data initialization...");

        // Check if data already exists
        if (userRepository.count() > 0 || churchRepository.count() > 0 || departmentRepository.count() > 0) {
            log.info("Data already exists, skipping initialization");
            return;
        }

        CompletableFuture<Void> userTask = CompletableFuture.runAsync(this::initializeUsers, executorService);
        CompletableFuture<Void> churchTask = CompletableFuture.runAsync(this::initializeChurches, executorService);
        CompletableFuture<Void> departmentTask = CompletableFuture.runAsync(this::initializeDepartments, executorService);

        // Wait for all tasks to complete
        try {
            CompletableFuture.allOf(userTask, churchTask, departmentTask).get();
            progressService.setInitializationComplete(true);
            log.info("All data initialization completed successfully!");

            // Launch frontend application after successful initialization
            log.info("🚀 Backend initialization complete - launching frontend application...");
            frontendLauncherService.launchFrontendIfReady();

        } catch (Exception e) {
            log.error("Error during data initialization", e);
        } finally {
            executorService.shutdown();
        }
    }

    private void initializeUsers() {
        try {
            progressService.updateProgress("User", "Starting user data population...");
            log.info("Populating User data...");

            userRepository.saveAllAndFlush(createMockUsers());

            progressService.updateProgress("User",
                    String.format("Populated %d users", createMockUsers().size()));

            progressService.updateProgress("User", "User data population completed!");
            log.info("User data population completed - {} users created", createMockUsers().size());

        } catch (Exception e) {
            log.error("Error initializing users", e);
            progressService.updateProgress("User", "Error: " + e.getMessage());
        }
    }

    private void initializeChurches() {
        try {
            progressService.updateProgress("Church", "Starting church data population...");
            log.info("Populating Church data...");

            List<Church> churches = createMockChurches();

            for (int i = 0; i < churches.size(); i++) {
                churchRepository.save(churches.get(i));
                progressService.updateProgress("Church",
                        String.format("Populated %d/%d churches", i + 1, churches.size()));
                // Simulate processing time
                Thread.sleep(100);
            }

            progressService.updateProgress("Church", "Church data population completed!");
            log.info("Church data population completed - {} churches created", churches.size());

        } catch (Exception e) {
            log.error("Error initializing churches", e);
            progressService.updateProgress("Church", "Error: " + e.getMessage());
        }
    }

    private void initializeDepartments() {
        try {
            progressService.updateProgress("Department", "Starting department data population...");
            log.info("Populating Department data...");

            List<Department> departments = createMockDepartments();

            for (int i = 0; i < departments.size(); i++) {
                departmentRepository.save(departments.get(i));
                progressService.updateProgress("Department",
                        String.format("Populated %d/%d departments", i + 1, departments.size()));
                // Simulate processing time
                Thread.sleep(75);
            }

            progressService.updateProgress("Department", "Department data population completed!");
            log.info("Department data population completed - {} departments created", departments.size());

        } catch (Exception e) {
            log.error("Error initializing departments", e);
            progressService.updateProgress("Department", "Error: " + e.getMessage());
        }
    }

    private List<User> createMockUsers() {
        List<User> users = new ArrayList<>();

        for (int i = 1; i <= 50; i++) {
            // Generate realistic address using Faker
            Address address = new Address(
                    faker.address().streetAddress(),
                    faker.address().city(),
                    faker.address().state(),
                    faker.address().zipCode(),
                    faker.address().country(),
                    faker.address().countryCode());

            // Generate realistic user data
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String email = faker.internet().emailAddress(firstName.toLowerCase() + "." + lastName.toLowerCase());
            String phone = faker.phoneNumber().phoneNumber();
            String userName = firstName + lastName.substring(0, 3);
            String language = "English";
            String theme = "default";
            boolean notifications = faker.bool().bool();
            UserRole role = faker.options().option(UserRole.class);
            String password = "password123";
            String avatar = "https://api.dicebear.com/9.x/adventurer/svg?seed=" + firstName;

            UserPreferences preferences = new UserPreferences(
                    language, theme, notifications);

            UserProfile profile = new UserProfile(
                    firstName,
                    lastName,
                    avatar,
                    preferences
                    );

            User user = new User(
                    null,
                    userName,
                    address,
                    email,
                    phone,
                    new BCryptPasswordEncoder().encode(password),
                    profile,
                    role);
            users.add(user);
        }
        return users;
    }

    private List<Church> createMockChurches() {
        List<Church> churches = new ArrayList<>();
        String[] denominations = { "Baptist", "Methodist", "Presbyterian", "Lutheran", "Catholic",
                "Pentecostal", "Anglican", "Orthodox", "Episcopal", "Evangelical" };

        for (int i = 1; i <= 15; i++) {
            // Generate realistic church address
            Address address = new Address(
                    faker.address().streetAddress(),
                    faker.address().city(),
                    faker.address().state(),
                    faker.address().zipCode(),
                    faker.address().country(),
                    faker.address().countryCode());

            // Generate realistic church data
            String churchName = faker.company().name() + " " +
                    denominations[faker.random().nextInt(denominations.length)] + " Church";
            String denomination = denominations[faker.random().nextInt(denominations.length)];
            String pastorName = "Pastor " + faker.name().firstName() + " " + faker.name().lastName();
            String email = faker.internet().emailAddress(churchName.toLowerCase().replaceAll("[^a-z0-9]", ""));
            String phone = faker.phoneNumber().phoneNumber();
            Integer capacity = faker.number().numberBetween(50, 500);

            Church church = new Church(
                    null,
                    churchName,
                    denomination,
                    address,
                    email,
                    phone,
                    pastorName,
                    capacity);
            churches.add(church);

        }
        return churches;
    }

    private List<Department> createMockDepartments() {
        List<Department> departments = new ArrayList<>();
        String[] deptNames = { "Youth Ministry", "Music Ministry", "Outreach", "Education", "Finance",
                "Administration", "Children's Ministry", "Senior Ministry", "Worship", "Missions",
                "Community Outreach", "Pastoral Care", "Media Ministry", "Hospitality", "Security" };

        for (int i = 0; i < deptNames.length; i++) {
            // Generate realistic department data using Faker
            String deptName = deptNames[i];
            String description = faker.lorem().sentence(8, 4); // Generate realistic description
            String headName = faker.name().firstName() + " " + faker.name().lastName();
            String email = faker.internet().emailAddress(deptName.toLowerCase().replaceAll("[^a-z0-9]", ""));
            String phone = faker.phoneNumber().phoneNumber();
            Integer memberCount = faker.number().numberBetween(5, 50);

            // Generate realistic budget using currency format
            double budgetAmount = faker.number().randomDouble(2, 5000, 50000);
            String budget = currencyFormat.format(budgetAmount);

            Department department = new Department(
                    null,
                    deptName,
                    description,
                    headName,
                    email,
                    phone,
                    memberCount,
                    budget);
            departments.add(department);
        }
        return departments;
    }
}
