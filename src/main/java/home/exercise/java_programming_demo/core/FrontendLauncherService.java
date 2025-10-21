package home.exercise.java_programming_demo.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class FrontendLauncherService {

    @Value("${app.frontend.path:E:/2025/storyLine/react_demo/sda_acm_2025/sda_acm_webapp}")
    private String frontendPath;

    @Value("${app.frontend.auto-launch:true}")
    private boolean autoLaunch;

    @Value("${app.frontend.port:3000}")
    private int frontendPort;

    private final AtomicBoolean frontendLaunched = new AtomicBoolean(false);
    private Process frontendProcess;

    public void launchFrontendIfReady() {
        if (!autoLaunch) {
            log.info("Frontend auto-launch is disabled");
            return;
        }

        if (frontendLaunched.get()) {
            log.info("Frontend already launched");
            return;
        }

        if (!isFrontendDirectoryValid()) {
            log.warn("Frontend directory not found or invalid: {}", frontendPath);
            return;
        }

        if (isPortInUse(frontendPort)) {
            log.info("Frontend port {} is already in use, skipping launch", frontendPort);
            frontendLaunched.set(true);
            return;
        }

        CompletableFuture.runAsync(this::startFrontendApplication);
    }

    private boolean isFrontendDirectoryValid() {
        File frontendDir = new File(frontendPath);
        if (!frontendDir.exists() || !frontendDir.isDirectory()) {
            return false;
        }

        // Check if package.json exists
        File packageJson = new File(frontendDir, "package.json");
        return packageJson.exists();
    }

    private boolean isPortInUse(int port) {
        try {
            ProcessBuilder pb = new ProcessBuilder("netstat", "-ano");
            Process process = pb.start();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(":" + port + " ") && line.contains("LISTENING")) {
                        return true;
                    }
                }
            }
            process.waitFor();
        } catch (Exception e) {
            log.debug("Error checking port usage: {}", e.getMessage());
        }
        return false;
    }

    private void startFrontendApplication() {
        try {
            log.info("🚀 Starting React frontend application...");
            log.info("Frontend path: {}", frontendPath);

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(frontendPath));
            
            // Use cmd on Windows to run npm
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                processBuilder.command("cmd", "/c", "npm", "run", "start");
            } else {
                processBuilder.command("npm", "run", "start");
            }

            // Redirect output to capture logs
            processBuilder.redirectErrorStream(true);
            
            frontendProcess = processBuilder.start();
            frontendLaunched.set(true);

            log.info("✅ Frontend application started successfully!");
            log.info("🌐 Frontend should be available at: http://localhost:{}", frontendPort);

            // Monitor the process output
            monitorFrontendProcess();

        } catch (IOException e) {
            log.error("❌ Failed to start frontend application: {}", e.getMessage());
            log.error("Make sure npm is installed and the frontend directory exists");
        }
    }

    private void monitorFrontendProcess() {
        CompletableFuture.runAsync(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(frontendProcess.getInputStream()))) {
                String line;
                boolean serverStarted = false;
                
                while ((line = reader.readLine()) != null) {
                    // Log important frontend messages
                    if (line.contains("webpack compiled") || 
                        line.contains("Local:") || 
                        line.contains("On Your Network:") ||
                        line.contains("compiled successfully") ||
                        line.contains("Failed to compile")) {
                        log.info("Frontend: {}", line.trim());
                    }
                    
                    // Detect when the server is ready
                    if (!serverStarted && (line.contains("webpack compiled") || line.contains("compiled successfully"))) {
                        serverStarted = true;
                        log.info("🎉 Frontend application is ready and compiled successfully!");
                        
                        // Optional: Open browser automatically
                        openBrowserIfConfigured();
                    }
                    
                    // Log errors
                    if (line.contains("ERROR") || line.contains("Failed")) {
                        log.warn("Frontend Warning/Error: {}", line.trim());
                    }
                }
            } catch (IOException e) {
                log.debug("Frontend process monitoring ended: {}", e.getMessage());
            }
        });
    }

    private void openBrowserIfConfigured() {
        // Optional: Auto-open browser (can be configured)
        try {
            String url = "http://localhost:" + frontendPort;
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                ProcessBuilder pb = new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url);
                pb.start();
                log.info("🌐 Opened browser to: {}", url);
            }
        } catch (Exception e) {
            log.debug("Could not auto-open browser: {}", e.getMessage());
        }
    }

    public boolean isFrontendRunning() {
        return frontendLaunched.get() && (frontendProcess != null && frontendProcess.isAlive());
    }

    public void stopFrontend() {
        if (frontendProcess != null && frontendProcess.isAlive()) {
            log.info("🛑 Stopping frontend application...");
            frontendProcess.destroyForcibly();
            frontendLaunched.set(false);
            log.info("✅ Frontend application stopped");
        }
    }

    public String getFrontendStatus() {
        if (!autoLaunch) {
            return "Auto-launch disabled";
        }
        if (isFrontendRunning()) {
            return "Running on port " + frontendPort;
        }
        if (frontendLaunched.get()) {
            return "Launched but process ended";
        }
        return "Not started";
    }
}
