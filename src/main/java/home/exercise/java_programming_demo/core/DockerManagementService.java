package home.exercise.java_programming_demo.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class DockerManagementService {

    private static final String BASH_SCRIPT_DIR = "bash";
    private static final String DOCKER_SETUP_SCRIPT = "docker-setup.sh";

    public boolean isDockerRunning() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("docker", "info");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            log.error("Error checking Docker status", e);
            return false;
        }
    }

    public boolean isPostgreSQLContainerRunning() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("docker", "ps", "--filter", "name=postgres", "--format", "table {{.Names}}");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("postgres") && !line.contains("NAMES")) {
                    found = true;
                    break;
                }
            }
            
            int exitCode = process.waitFor();
            return exitCode == 0 && found;
        } catch (Exception e) {
            log.error("Error checking PostgreSQL container status", e);
            return false;
        }
    }

    public void setupDockerEnvironment() throws IOException, InterruptedException {
        log.info("Setting up Docker environment...");
        
        // Create bash directory if it doesn't exist
        Path bashDir = Paths.get(BASH_SCRIPT_DIR);
        File bashDirFile = bashDir.toFile();
        if (!bashDirFile.exists()) {
            bashDirFile.mkdirs();
            log.info("Created bash directory: {}", bashDir.toAbsolutePath());
        }

        // Create the Docker setup script
        createDockerSetupScript();
        
        // Execute the script
        executeDockerSetupScript();
    }

    private void createDockerSetupScript() throws IOException {
        Path scriptPath = Paths.get(BASH_SCRIPT_DIR, DOCKER_SETUP_SCRIPT);
        String scriptContent = generateDockerSetupScript();
        
        java.nio.file.Files.write(scriptPath, scriptContent.getBytes());
        log.info("Created Docker setup script: {}", scriptPath.toAbsolutePath());
        
        // Make script executable (Unix-like systems)
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            try {
                ProcessBuilder pb = new ProcessBuilder("chmod", "+x", scriptPath.toString());
                pb.start().waitFor();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted while making script executable", e);
            }
        }
    }

    private String generateDockerSetupScript() {
        return """
            #!/bin/bash
            
            echo "Starting Docker PostgreSQL setup..."
            
            # Check if Docker is running
            if ! docker info > /dev/null 2>&1; then
                echo "Docker is not running. Please start Docker first."
                exit 1
            fi
            
            # Stop existing PostgreSQL container if running
            if docker ps -q --filter "name=postgres" | grep -q .; then
                echo "Stopping existing PostgreSQL container..."
                docker stop postgres
                docker rm postgres
            fi
            
            # Remove existing PostgreSQL container if exists
            if docker ps -aq --filter "name=postgres" | grep -q .; then
                echo "Removing existing PostgreSQL container..."
                docker rm postgres
            fi
            
            # Pull PostgreSQL image
            echo "Pulling PostgreSQL image..."
            docker pull postgres:15
            
            # Create and start PostgreSQL container
            echo "Creating PostgreSQL container..."
            docker run --name postgres \\
                -e POSTGRES_DB=mydatabase \\
                -e POSTGRES_USER=myuser \\
                -e POSTGRES_PASSWORD=mypassword \\
                -p 5432:5432 \\
                -d postgres:15
            
            # Wait for PostgreSQL to be ready
            echo "Waiting for PostgreSQL to be ready..."
            sleep 10
            
            # Check if container is running
            if docker ps --filter "name=postgres" --filter "status=running" | grep -q postgres; then
                echo "PostgreSQL container is running successfully!"
                echo "Database: mydatabase"
                echo "User: myuser"
                echo "Password: mypassword"
                echo "Port: 5432"
            else
                echo "Failed to start PostgreSQL container"
                exit 1
            fi
            
            echo "Docker PostgreSQL setup completed successfully!"
            """;
    }

    private void executeDockerSetupScript() throws IOException, InterruptedException {
        Path scriptPath = Paths.get(BASH_SCRIPT_DIR, DOCKER_SETUP_SCRIPT);
        
        ProcessBuilder processBuilder;
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            // For Windows, use Git Bash or WSL
            processBuilder = new ProcessBuilder("bash", scriptPath.toString());
        } else {
            // For Unix-like systems
            processBuilder = new ProcessBuilder("bash", scriptPath.toString());
        }
        
        processBuilder.directory(new File("."));
        processBuilder.redirectErrorStream(true);
        
        log.info("Executing Docker setup script: {}", scriptPath.toAbsolutePath());
        
        Process process = processBuilder.start();
        
        // Read and log output
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            log.info("Script output: {}", line);
        }
        
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            log.info("Docker setup script executed successfully");
        } else {
            log.error("Docker setup script failed with exit code: {}", exitCode);
            throw new RuntimeException("Docker setup failed");
        }
    }

    public String getDockerStatus() {
        StringBuilder status = new StringBuilder();
        
        status.append("Docker Status:\n");
        status.append("- Docker Running: ").append(isDockerRunning()).append("\n");
        status.append("- PostgreSQL Container Running: ").append(isPostgreSQLContainerRunning()).append("\n");
        
        return status.toString();
    }
}
