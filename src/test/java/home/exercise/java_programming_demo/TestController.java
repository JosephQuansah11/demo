package home.exercise.java_programming_demo;

import com.github.javafaker.Faker;
import home.exercise.java_programming_demo.core.DataInitializationProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    private final DataInitializationProgressService progressService = new DataInitializationProgressService();
    private final Faker faker = new Faker();

    @PostMapping("/simulate-change")
    public ResponseEntity<Map<String, String>> simulateChange(
            @RequestParam(defaultValue = "Test") String entityType,
            @RequestParam(required = false) String message) {
        
        // Generate realistic message using Faker if not provided
        String actualMessage = message != null ? message : 
            faker.company().buzzword() + " " + faker.hacker().verb() + " " + faker.number().digits(2) + " records";
        
        // Simulate a progress update to test intelligent polling
        progressService.updateProgress(entityType, actualMessage);
        
        return ResponseEntity.ok(Map.of(
            "message", "Simulated change triggered with realistic data",
            "entityType", entityType,
            "updateMessage", actualMessage,
            "generated", message == null ? "true" : "false"
        ));
    }

    @PostMapping("/reset-initialization")
    public ResponseEntity<Map<String, String>> resetInitialization() {
        // Reset initialization status to test polling behavior
        progressService.setInitializationComplete(false);
        progressService.updateProgress("System", "Initialization reset for testing");
        
        return ResponseEntity.ok(Map.of(
            "message", "Initialization status reset",
            "status", "System will now show as active for polling"
        ));
    }

    @PostMapping("/complete-initialization")
    public ResponseEntity<Map<String, String>> completeInitialization() {
        // Mark initialization as complete
        progressService.setInitializationComplete(true);
        progressService.updateProgress("System", "Initialization marked as complete");
        
        return ResponseEntity.ok(Map.of(
            "message", "Initialization marked as complete",
            "status", "System will now go idle"
        ));
    }
}
