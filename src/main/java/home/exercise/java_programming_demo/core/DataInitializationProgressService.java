package home.exercise.java_programming_demo.core;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class DataInitializationProgressService {

    private final Map<String, ProgressInfo> progressMap = new ConcurrentHashMap<>();
    private final AtomicBoolean initializationComplete = new AtomicBoolean(false);
    private final AtomicBoolean hasActiveChanges = new AtomicBoolean(false);
    private final AtomicLong lastUpdateTime = new AtomicLong(System.currentTimeMillis());
    private final AtomicLong changeVersion = new AtomicLong(0);

    public void updateProgress(String entityType, String message) {
        ProgressInfo progress = new ProgressInfo();
        progress.setEntityType(entityType);
        progress.setMessage(message);
        progress.setTimestamp(LocalDateTime.now());
        progress.setVersion(changeVersion.incrementAndGet());
        
        progressMap.put(entityType, progress);
        lastUpdateTime.set(System.currentTimeMillis());
        hasActiveChanges.set(true);
        
        log.info("Progress Update - {}: {}", entityType, message);
    }

    public Map<String, ProgressInfo> getAllProgress() {
        return new ConcurrentHashMap<>(progressMap);
    }

    public ProgressInfo getProgress(String entityType) {
        return progressMap.get(entityType);
    }

    public boolean isInitializationComplete() {
        return initializationComplete.get();
    }

    public void setInitializationComplete(boolean complete) {
        this.initializationComplete.set(complete);
        if (complete) {
            hasActiveChanges.set(false); // Mark as idle when complete
        }
    }

    public boolean hasActiveChanges() {
        return hasActiveChanges.get();
    }

    public long getLastUpdateTime() {
        return lastUpdateTime.get();
    }

    public long getCurrentVersion() {
        return changeVersion.get();
    }

    public void markAsIdle() {
        hasActiveChanges.set(false);
    }

    public boolean shouldPoll() {
        // Only poll if there are active changes or if initialization is not complete
        return hasActiveChanges.get() || !initializationComplete.get();
    }

    @Data
    public static class ProgressInfo {
        private String entityType;
        private String message;
        private LocalDateTime timestamp;
        private long version;
    }
}
