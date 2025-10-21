package home.exercise.java_programming_demo.controller;

import home.exercise.java_programming_demo.core.DockerManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/docker")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class DockerController {

    private final DockerManagementService dockerService;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getDockerStatus() {
        Map<String, Object> status = Map.of(
            "dockerRunning", dockerService.isDockerRunning(),
            "postgresRunning", dockerService.isPostgreSQLContainerRunning(),
            "statusMessage", dockerService.getDockerStatus()
        );
        return ResponseEntity.ok(status);
    }

    @PostMapping("/setup")
    public ResponseEntity<Map<String, String>> setupDocker() {
        try {
            if (!dockerService.isDockerRunning()) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Docker is not running. Please start Docker first.")
                );
            }

            if (dockerService.isPostgreSQLContainerRunning()) {
                return ResponseEntity.ok(
                    Map.of("message", "PostgreSQL container is already running")
                );
            }

            dockerService.setupDockerEnvironment();
            return ResponseEntity.ok(
                Map.of("message", "Docker environment setup completed successfully")
            );
        } catch (Exception e) {
            log.error("Error setting up Docker environment", e);
            return ResponseEntity.internalServerError().body(
                Map.of("error", "Failed to setup Docker environment: " + e.getMessage())
            );
        }
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkDockerServices() {
        return ResponseEntity.ok(Map.of(
            "docker", dockerService.isDockerRunning(),
            "my_postgres_db", dockerService.isPostgreSQLContainerRunning()
        ));
    }
}
