package home.exercise.java_programming_demo.controller;

import home.exercise.java_programming_demo.core.DataInitializationProgressService;
import home.exercise.java_programming_demo.core.FrontendLauncherService;
import home.exercise.java_programming_demo.db.Church;
import home.exercise.java_programming_demo.db.Department;
import home.exercise.java_programming_demo.db.User;
import home.exercise.java_programming_demo.db.service.ChurchRepository;
import home.exercise.java_programming_demo.db.service.DepartmentRepository;
import home.exercise.java_programming_demo.db.service.UserServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DataController {

    private final UserServiceRepository userRepository;
    private final ChurchRepository churchRepository;
    private final DepartmentRepository departmentRepository;
    private final DataInitializationProgressService progressService;
    private final FrontendLauncherService frontendLauncherService;

    @GetMapping("/progress")
    public ResponseEntity<Map<String, Object>> getInitializationProgress(
            @RequestParam(defaultValue = "0") long clientVersion) {

        Map<String, Object> response = new HashMap<>();
        long currentVersion = progressService.getCurrentVersion();
        System.out.println("\n\\n" + //
                "\\n" + //
                "\\n" + //
                "Current version: " + currentVersion + //
                "\\n" + //
                "\\n" + "Client version: " + clientVersion + //
                "\\n");

        // Only return data if client version is outdated or if there are active changes
        if (clientVersion < currentVersion || progressService.hasActiveChanges()) {
            response.put("progress", progressService.getAllProgress());
            response.put("version", currentVersion);
            response.put("hasChanges", progressService.hasActiveChanges());
            response.put("shouldPoll", progressService.shouldPoll());
        } else {
            // No changes, return minimal response
            response.put("version", currentVersion);
            response.put("hasChanges", false);
            response.put("shouldPoll", progressService.shouldPoll());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/progress/{entityType}")
    public ResponseEntity<DataInitializationProgressService.ProgressInfo> getEntityProgress(
            @PathVariable String entityType) {
        DataInitializationProgressService.ProgressInfo progress = progressService.getProgress(entityType);
        if (progress != null) {
            return ResponseEntity.ok(progress);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getDataStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("userCount", userRepository.count());
        status.put("churchCount", churchRepository.count());
        status.put("departmentCount", departmentRepository.count());
        status.put("complete", progressService.isInitializationComplete());

        // Add frontend status
        Map<String, Object> frontendStatus = new HashMap<>();
        frontendStatus.put("running", frontendLauncherService.isFrontendRunning());
        frontendStatus.put("status", frontendLauncherService.getFrontendStatus());
        status.put("frontend", frontendStatus);

        return ResponseEntity.ok(status);
    }

    @PostMapping("/launch-frontend")
    public ResponseEntity<Map<String, String>> launchFrontend() {
        frontendLauncherService.launchFrontendIfReady();
        return ResponseEntity.ok(Map.of(
                "message", "Frontend launch initiated",
                "status", frontendLauncherService.getFrontendStatus()));
    }

    @PostMapping("/stop-frontend")
    public ResponseEntity<Map<String, String>> stopFrontend() {
        frontendLauncherService.stopFrontend();
        return ResponseEntity.ok(Map.of(
                "message", "Frontend stop initiated",
                "status", frontendLauncherService.getFrontendStatus()));
    }

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userRepository.findAll(pageable));
    }

    @GetMapping("/churches")
    public ResponseEntity<Page<Church>> getChurches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(churchRepository.findAll(pageable));
    }

    @GetMapping("/departments")
    public ResponseEntity<Page<Department>> getDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(departmentRepository.findAll(pageable));
    }

    @GetMapping("/users/count")
    public ResponseEntity<Integer> getUserCount() {
        return ResponseEntity.ok(userRepository.findAll().size());
    }

    @GetMapping("/churches/count")
    public ResponseEntity<Integer> getChurchCount() {
        return ResponseEntity.ok(churchRepository.findAll().size());
    }

    @GetMapping("/departments/count")
    public ResponseEntity<Integer> getDepartmentCount() {
        return ResponseEntity.ok(departmentRepository.findAll().size());
    }
}
